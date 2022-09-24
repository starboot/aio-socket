package io.github.mxd888.socket.utils.pool.memory;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ByteBuffer内存块
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class MemoryChunk {

    /**
     * 条件锁
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 当前缓存页的物理缓冲区
     */
    private final ByteBuffer buffer;

    /**
     * 待回收的虚拟Buffer
     */
    private final ConcurrentLinkedQueue<MemoryCell> cleanBuffers = new ConcurrentLinkedQueue<>();

    /**
     * 当前空闲的虚拟Buffer
     */
    private final List<MemoryCell> availableBuffers;

    /**
     * 内存页是否处于空闲状态
     */
    private boolean idle = true;


    /**
     * 默认范围构造方法，只允许本包内进行构造
     *
     * @param size   缓存页大小
     * @param direct 是否使用堆外内存
     */
    MemoryChunk(int size, boolean direct) {
        availableBuffers = new LinkedList<>();
        this.buffer = allocate0(size, direct);
        availableBuffers.add(new MemoryCell(this, null, buffer.position(), buffer.limit()));
    }

    /**
     * 申请物理内存页空间
     *
     * @param size   物理空间大小
     * @param direct true:堆外缓冲区,false:堆内缓冲区
     * @return       缓冲区
     */
    private ByteBuffer allocate0(int size, boolean direct) {
        return direct ? ByteBuffer.allocateDirect(size) : ByteBuffer.allocate(size);
    }

    /**
     * 申请虚拟内存
     *
     * @param size 申请大小
     * @return     虚拟内存对象
     */
    public MemoryCell allocate(final int size) {
        MemoryCell virtualBuffer = allocate0(size);
        if (virtualBuffer == null) {
            System.out.println("开始申请堆内内存");
        }
        return virtualBuffer == null ? new MemoryCell(null, allocate0(size, false), 0, 0) : virtualBuffer;
    }

    /**
     * 申请虚拟内存
     *
     * @param size 申请大小
     * @return     虚拟内存对象
     */
    private MemoryCell allocate0(final int size) {
        idle = false;
        MemoryCell cleanBuffer = cleanBuffers.poll();
        if (cleanBuffer != null && cleanBuffer.getCapacity() >= size) {
            cleanBuffer.buffer().clear();
            cleanBuffer.buffer(cleanBuffer.buffer());
            return cleanBuffer;
        }
        lock.lock();
        try {
            if (cleanBuffer != null) {
                clean0(cleanBuffer);
                while ((cleanBuffer = cleanBuffers.poll()) != null) {
                    if (cleanBuffer.getCapacity() >= size) {
                        cleanBuffer.buffer().clear();
                        cleanBuffer.buffer(cleanBuffer.buffer());
                        return cleanBuffer;
                    } else {
                        clean0(cleanBuffer);
                    }
                }
            }

            int count = availableBuffers.size();
            MemoryCell bufferChunk = null;
            //仅剩一个可用内存块的时候使用快速匹配算法
            if (count == 1) {
                bufferChunk = fastAllocate(size);
            } else if (count > 1) {
                bufferChunk = slowAllocate(size);
            }
            return bufferChunk;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 快速匹配
     *
     * @param size 申请内存大小
     * @return     申请到的内存块, 若空间不足则范围null
     */
    private MemoryCell fastAllocate(int size) {
        MemoryCell freeChunk = availableBuffers.get(0);
        MemoryCell bufferChunk = allocate(size, freeChunk);
        if (freeChunk == bufferChunk) {
            availableBuffers.clear();
        }
        return bufferChunk;
    }

    /**
     * 迭代申请
     *
     * @param size 申请内存大小
     * @return     申请到的内存块, 若空间不足则范围null
     */
    private MemoryCell slowAllocate(int size) {
        Iterator<MemoryCell> iterator = availableBuffers.listIterator(0);
        MemoryCell bufferChunk;
        while (iterator.hasNext()) {
            MemoryCell freeChunk = iterator.next();
            bufferChunk = allocate(size, freeChunk);
            if (freeChunk == bufferChunk) {
                iterator.remove();
            }
            if (bufferChunk != null) {
                return bufferChunk;
            }
        }
        return null;
    }

    /**
     * 从可用内存大块中申请所需的内存小块
     *
     * @param size      申请内存大小
     * @param freeChunk 可用于申请的内存块
     * @return          申请到的内存块, 若空间不足则范围null
     */
    private MemoryCell allocate(int size, MemoryCell freeChunk) {
        final int capacity = freeChunk.getCapacity();
        if (capacity < size) {
            return null;
        }
        MemoryCell bufferChunk;
        if (capacity == size) {
            buffer.limit(freeChunk.getParentLimit());
            buffer.position(freeChunk.getParentPosition());
            freeChunk.buffer(buffer.slice());
            bufferChunk = freeChunk;
        } else {
            buffer.limit(freeChunk.getParentPosition() + size);
            buffer.position(freeChunk.getParentPosition());
            bufferChunk = new MemoryCell(this, buffer.slice(), buffer.position(), buffer.limit());
            freeChunk.setParentPosition(buffer.limit());
        }
        if (bufferChunk.buffer().remaining() != size) {
            throw new RuntimeException("allocate " + size + ", io.github.mxd888.socket.buffer:" + bufferChunk);
        }
        return bufferChunk;
    }


    /**
     * 内存回收
     *
     * @param cleanBuffer 待回收的虚拟内存
     */
    void clean(MemoryCell cleanBuffer) {
        cleanBuffers.offer(cleanBuffer);
    }

    /**
     * 尝试回收缓冲区
     */
    void tryClean() {
        //下个周期依旧处于空闲则触发回收任务
        if (!idle) {
            idle = true;
        } else if (!cleanBuffers.isEmpty() && lock.tryLock()) {
            try {
                MemoryCell cleanBuffer;
                while ((cleanBuffer = cleanBuffers.poll()) != null) {
                    clean0(cleanBuffer);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 回收虚拟缓冲区：修正
     *
     * @param cleanBuffer 虚拟缓冲区
     */
    private void clean0(MemoryCell cleanBuffer) {
        ListIterator<MemoryCell> iterator = availableBuffers.listIterator(0);
        while (iterator.hasNext()) {
            MemoryCell freeBuffer = iterator.next();
            //cleanBuffer在freeBuffer之前并且形成连续块
            if (freeBuffer.getParentPosition() == cleanBuffer.getParentLimit()) {
                freeBuffer.setParentPosition(cleanBuffer.getParentPosition());
                return;
            }
            //cleanBuffer与freeBuffer之后并形成连续块
            if (freeBuffer.getParentLimit() == cleanBuffer.getParentPosition()) {
                freeBuffer.setParentLimit(cleanBuffer.getParentLimit());
                //判断后一个是否连续
                if (iterator.hasNext()) {
                    MemoryCell next = iterator.next();
                    if (next.getParentPosition() == freeBuffer.getParentLimit()) {
                        freeBuffer.setParentLimit(next.getParentLimit());
                        iterator.remove();
                    } else if (next.getParentPosition() < freeBuffer.getParentLimit()) {
                        throw new IllegalStateException("");
                    }
                }
                return;
            }
            if (freeBuffer.getParentPosition() > cleanBuffer.getParentLimit()) {
                iterator.previous();
                iterator.add(cleanBuffer);
                return;
            }
        }
        iterator.add(cleanBuffer);
    }

    /**
     * 释放内存
     */
    void release() {
        if (buffer.isDirect()) {
            DirectBuffer directBuffer = (DirectBuffer) buffer;
            if (Objects.nonNull(directBuffer.cleaner())) {
                directBuffer.cleaner().clean();
            }
        }
    }

    @Override
    public String toString() {
        return "BufferPage{availableBuffers=" + availableBuffers + ", cleanBuffers=" + cleanBuffers + '}';
    }
}
