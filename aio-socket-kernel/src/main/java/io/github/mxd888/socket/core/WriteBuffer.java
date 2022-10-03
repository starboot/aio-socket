/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.github.mxd888.socket.core;

import io.github.mxd888.socket.utils.pool.buffer.BufferPage;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * 包装当前会话分配到的虚拟Buffer,提供流式操作方式
 * 首先将待发送的数据暂存到writeInBuf
 * 在执行发送，若没有人往这个通道写入数据则立刻发送
 * 否则存进待发送队列items
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class WriteBuffer extends OutputStream {

    /**
     * 存储已就绪待输出的数据
     */
    private final VirtualBuffer[] items;

    /**
     * 为当前 WriteBuffer 提供数据存放功能的缓存页 用于申请内存空间
     */
    private final BufferPage bufferPage;

    /**
     * 缓冲区数据刷新Function，执行发送的具体逻辑函数
     */
    private final Consumer<WriteBuffer> consumer;

    /**
     * 默认内存块大小 写操作所申请的空间初始大小，默认128字节
     */
    private final int chunkSize;

    /**
     * items 读索引位
     */
    private int takeIndex;

    /**
     * items 写索引位
     */
    private int putIndex;

    /**
     * items 中存放的缓冲数据数量
     */
    private int count;

    /**
     * 暂存当前业务正在输出的数据,输出完毕后会存放到items中
     */
    private VirtualBuffer writeInBuf;

    /**
     * 当前WriteBuffer是否已关闭
     */
    private boolean closed = false;

    /**
     * 辅助数组
     */
    private byte[] bytes;

    public WriteBuffer(BufferPage bufferPage, Consumer<WriteBuffer> consumer, int chunkSize, int capacity) {
        this.bufferPage = bufferPage;
        this.consumer = consumer;
        this.items = new VirtualBuffer[capacity];
        this.chunkSize = chunkSize;
    }

    /**
     * 获取一个虚拟空间用于编码操作
     *
     * @return 虚拟空间
     */
    public VirtualBuffer newVirtualBuffer(int size) {
        return bufferPage.allocate(size);
    }

    @Override
    public void write(int b) {
        writeByte((byte) b);
    }

    public void writeShort(short v) throws IOException {
        byte[] bytes = initCacheBytes();
        bytes[0] = (byte) ((v >>> 8) & 0xFF);
        bytes[1] = (byte) (v & 0xFF);
        write(bytes, 0, 2);
    }

    public synchronized void writeByte(byte b) {
        if (writeInBuf == null) {
            writeInBuf = bufferPage.allocate(chunkSize);
        }
        writeInBuf.buffer().put(b);
        flushWriteBuffer(false);
    }

    public void writeInt(int v) throws IOException {
        byte[] bytes = initCacheBytes();
        bytes[0] = (byte) ((v >>> 24) & 0xFF);
        bytes[1] = (byte) ((v >>> 16) & 0xFF);
        bytes[2] = (byte) ((v >>> 8) & 0xFF);
        bytes[3] = (byte) (v & 0xFF);
        write(bytes, 0, 4);
    }

    /**
     * 输出long数值,占用8个字节
     *
     * @param v long数值
     * @throws IOException IO异常
     */
    public void writeLong(long v) throws IOException {
        byte[] bytes = initCacheBytes();
        bytes[0] = (byte) ((v >>> 56) & 0xFF);
        bytes[1] = (byte) ((v >>> 48) & 0xFF);
        bytes[2] = (byte) ((v >>> 40) & 0xFF);
        bytes[3] = (byte) ((v >>> 32) & 0xFF);
        bytes[4] = (byte) ((v >>> 24) & 0xFF);
        bytes[5] = (byte) ((v >>> 16) & 0xFF);
        bytes[6] = (byte) ((v >>> 8) & 0xFF);
        bytes[7] = (byte) (v & 0xFF);
        write(bytes, 0, 8);
    }

    /**
     * 高并发的难处理之处（极易发生死锁），当传入int后离开write方法，但没有传输完成所以就没有flush（即，没有释放锁）；
     * 因为离开了write方法，所以另一个线程可以进来了，它进来后由于没有获得锁所以处于等待中，
     * 而由于之前的还没传输完所以它有来传输，由于write方法被锁住所以进不来
     * 即服务器处于死锁状态
     * @param b                 待输出的byte数组
     * @param off               相对位置
     * @param len               有效长度
     * @throws IOException      IO异常
     */
    @SuppressWarnings("all")
    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (writeInBuf == null) {
            writeInBuf = bufferPage.allocate(Math.max(chunkSize, len));
        }
        ByteBuffer writeBuffer = writeInBuf.buffer();
        if (closed) {
            writeInBuf.clean();
            writeInBuf = null;
            throw new IOException("writeBuffer has closed");
        }
        int remaining = writeBuffer.remaining();
        if (remaining > len) {
            writeBuffer.put(b, off, len);
        } else {
            writeBuffer.put(b, off, remaining);
            flushWriteBuffer(true);
            if (len > remaining) {
                write(b, off + remaining, len - remaining);
            }
        }

    }

    private byte[] initCacheBytes() {
        if (bytes != null) {
            return bytes;
        }
        synchronized (WriteBuffer.class) {
            if (bytes != null) {
                return bytes;
            }
            bytes = new byte[8];
            return bytes;
        }
    }

    /**
     * 把暂存的buffer发送出去
     *
     * @param forceFlush 是否立刻发送  强制冲洗，若通道正在被使用则将数据存入items数组
     */
    private void flushWriteBuffer(boolean forceFlush) {
        if (!forceFlush && writeInBuf.buffer().hasRemaining()) {
            return;
        }
        consumer.accept(this);
        // 检查是否已经发送出去了
        if (writeInBuf == null || writeInBuf.buffer().position() == 0) {
            return;
        }
        // 有人在发送，这个消息进入等待队列  writeInBuf修改为读模式
        writeInBuf.buffer().flip();
        VirtualBuffer virtualBuffer = writeInBuf;
        writeInBuf = null;
        try {
            while (count == items.length) {
                this.wait();
                //防止因close诱发内存泄露
                if (closed) {
                    virtualBuffer.clean();
                    return;
                }
            }

            items[putIndex] = virtualBuffer;
            if (++putIndex == items.length) {
                putIndex = 0;
            }
            count++;

        } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * 刷新缓冲区，将数据发送出去
     */
    public void flush() {
        if (closed) {
            throw new RuntimeException("OutputStream has closed");
        }
        if (this.count > 0 || (writeInBuf != null && writeInBuf.buffer().position() > 0)) {
            consumer.accept(this);
        }
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        flush();
        closed = true;
        if (writeInBuf != null) {
            writeInBuf.clean();
            writeInBuf = null;
        }
        VirtualBuffer byteBuf;
        while ((byteBuf = poll()) != null) {
            byteBuf.clean();
        }
    }


    /**
     * 是否存在待输出的数据
     *
     * @return true:有,false:无
     */
    boolean isEmpty() {
        return count == 0 && (writeInBuf == null || writeInBuf.buffer().position() == 0);
    }

    /**
     * 从带输出队列获取一个待发送消息
     *
     * @return VirtualBuffer类型的消息
     */
    VirtualBuffer pollItem() {
        if (count == 0) {
            return null;
        }
        synchronized (this) {
            VirtualBuffer x = items[takeIndex];
            items[takeIndex] = null;
            if (++takeIndex == items.length) {
                takeIndex = 0;
            }
            if (count-- == items.length) {
                this.notifyAll();
            }
            return x;
        }
    }

    /**
     * 获取并移除当前缓冲队列中头部的VirtualBuffer
     *
     * @return 待输出的VirtualBuffer
     */
    public synchronized VirtualBuffer poll() {
        VirtualBuffer item = pollItem();
        if (item != null) {
            return item;
        }
        if (writeInBuf != null && writeInBuf.buffer().position() > 0) {
            // 将暂存器里面的数据更改为读模式，一会将其读出来并发送
            writeInBuf.buffer().flip();
            VirtualBuffer buffer = writeInBuf;
            writeInBuf = null;
            return buffer;
        } else {
            return null;
        }
    }

}