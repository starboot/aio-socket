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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
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

public final class WriteBuffer {

    /**
     * 存储已就绪待输出的数据
     */
    private final ConcurrentLinkedQueue<VirtualBuffer> itemsQueue;

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
     * 暂存当前业务正在输出的数据,输出完毕后会存放到items中
     */
    private VirtualBuffer writeInBuf;

    /**
     * 当前WriteBuffer是否已关闭
     */
    private boolean closed = false;

    /**
     * 同步资源锁
     */
    private final ReentrantLock lock = new ReentrantLock();

    public WriteBuffer(BufferPage bufferPage, Consumer<WriteBuffer> consumer, int chunkSize) {
        this.bufferPage = bufferPage;
        this.consumer = consumer;
        this.itemsQueue = new ConcurrentLinkedQueue<>();
        this.chunkSize = chunkSize;
    }

    /**
     * 获取一个虚拟空间用于编码操作
     *
     * @return 虚拟空间
     */
    public VirtualBuffer newVirtualBuffer(int len) {
        if (!lock.isHeldByCurrentThread()) {
            lock.lock();
        }
        if (writeInBuf == null) {
            writeInBuf = bufferPage.allocate(Math.max(chunkSize, len));
        }else if (writeInBuf.buffer().remaining() < len) {
            flushWriteBuffer();
            writeInBuf = bufferPage.allocate(Math.max(chunkSize, len));
        }
        return writeInBuf;
    }

    /**
     * 把暂存的buffer发送出去
     */
    private void flushWriteBuffer() {
        consumer.accept(this);
        // 检查是否已经发送出去了
        if (writeInBuf == null || writeInBuf.buffer().position() == 0) {
            return;
        }
        // 有人在发送，这个消息进入等待队列  writeInBuf修改为读模式
        writeInBuf.buffer().flip();
        VirtualBuffer virtualBuffer = writeInBuf;
//        System.setProperty("java.vm.name","Java HotSpot(TM) ");
//        if (ObjectSizeCalculator.getObjectSize(itemsQueue) > 1024 * 1024 * 512) {
//            System.out.println("itemsQueue大于512M了");
//        }
        itemsQueue.offer(virtualBuffer);
        writeInBuf = null;
    }

    /**
     * 刷新缓冲区，将数据发送出去
     */
    public void flush() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
        if (closed) {
            throw new RuntimeException("OutputStream has closed");
        }
        if (!itemsQueue.isEmpty() || (writeInBuf != null && writeInBuf.buffer().position() > 0)) {
            consumer.accept(this);
        }

    }

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
        return itemsQueue.isEmpty() && (writeInBuf == null || writeInBuf.buffer().position() == 0);
    }

    /**
     * 从带输出队列获取一个待发送消息
     *
     * @return VirtualBuffer类型的消息
     */
    VirtualBuffer pollItem() {
        if (itemsQueue.isEmpty()) {
            return null;
        }
        return itemsQueue.poll();
    }

    /**
     * 获取并移除当前缓冲队列中头部的VirtualBuffer
     *
     * @return 待输出的VirtualBuffer
     */
    public VirtualBuffer poll() {
        VirtualBuffer item = pollItem();
        if (item != null) {
            return item;
        }
        boolean held = lock.isHeldByCurrentThread();
        if (held || lock.tryLock()) {
            if (writeInBuf != null && writeInBuf.buffer().position() > 0) {
                // 将暂存器里面的数据更改为读模式，一会将其读出来并发送
                writeInBuf.buffer().flip();
                VirtualBuffer buffer = writeInBuf;
                writeInBuf = null;
                if (!held) {
                    lock.unlock();
                }
                return buffer;
            } else {
                if (!held) {
                    lock.unlock();
                }
                return null;
            }
        }
        return null;
    }

}