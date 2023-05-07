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
package cn.starboot.socket.utils.pool.memory;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ByteBuffer内存页
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class MemoryBlock {

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
    private final ConcurrentLinkedQueue<MemoryUnit> cleanMemoryUnits = new ConcurrentLinkedQueue<>();

    /**
     * 当前空闲的虚拟Buffer
     */
    private final List<MemoryUnit> availableMemoryUnits;

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
    MemoryBlock(int size, boolean direct) {
		availableMemoryUnits = new LinkedList<>();
        this.buffer = allocate0(size, direct);
		availableMemoryUnits.add(new MemoryUnit(this, null, buffer.position(), buffer.limit()));
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
    public MemoryUnit allocate(final int size) {
        MemoryUnit memoryUnit = allocate0(size);
        return memoryUnit == null ? new MemoryUnit(null, allocate0(size, false), 0, 0) : memoryUnit;
    }

    /**
     * 申请虚拟内存
     *
     * @param size 申请大小
     * @return     虚拟内存对象
     */
    private MemoryUnit allocate0(final int size) {
        idle = false;
        MemoryUnit cleanBuffer = cleanMemoryUnits.poll();
        if (cleanBuffer != null && cleanBuffer.getCapacity() >= size) {
            cleanBuffer.buffer().clear();
            cleanBuffer.buffer(cleanBuffer.buffer());
            return cleanBuffer;
        }
        lock.lock();
        try {
            if (cleanBuffer != null) {
                clean0(cleanBuffer);
                while ((cleanBuffer = cleanMemoryUnits.poll()) != null) {
                    if (cleanBuffer.getCapacity() >= size) {
                        cleanBuffer.buffer().clear();
                        cleanBuffer.buffer(cleanBuffer.buffer());
                        return cleanBuffer;
                    } else {
                        clean0(cleanBuffer);
                    }
                }
            }

            int count = availableMemoryUnits.size();
            MemoryUnit bufferChunk = null;
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
    private MemoryUnit fastAllocate(int size) {
        MemoryUnit freeChunk = availableMemoryUnits.get(0);
        MemoryUnit bufferChunk = allocate(size, freeChunk);
        if (freeChunk == bufferChunk) {
			availableMemoryUnits.clear();
        }
        return bufferChunk;
    }

    /**
     * 迭代申请
     *
     * @param size 申请内存大小
     * @return     申请到的内存块, 若空间不足则范围null
     */
    private MemoryUnit slowAllocate(int size) {
        Iterator<MemoryUnit> iterator = availableMemoryUnits.listIterator(0);
        MemoryUnit bufferChunk;
        while (iterator.hasNext()) {
            MemoryUnit freeChunk = iterator.next();
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
    private MemoryUnit allocate(int size, MemoryUnit freeChunk) {
        final int capacity = freeChunk.getCapacity();
        if (capacity < size) {
            return null;
        }
        MemoryUnit bufferChunk;
        if (capacity == size) {
            buffer.limit(freeChunk.getParentLimit());
            buffer.position(freeChunk.getParentPosition());
            freeChunk.buffer(buffer.slice());
            bufferChunk = freeChunk;
        } else {
            buffer.limit(freeChunk.getParentPosition() + size);
            buffer.position(freeChunk.getParentPosition());
            bufferChunk = new MemoryUnit(this, buffer.slice(), buffer.position(), buffer.limit());
            freeChunk.setParentPosition(buffer.limit());
        }
        if (bufferChunk.buffer().remaining() != size) {
            throw new RuntimeException("allocate " + size + ", io.github.mxd888.socket.utils.pool.buffer:" + bufferChunk);
        }
        return bufferChunk;
    }


    /**
     * 内存回收
     *
     * @param cleanBuffer 待回收的虚拟内存
     */
    void clean(MemoryUnit cleanBuffer) {
		cleanMemoryUnits.offer(cleanBuffer);
    }

    /**
     * 尝试回收缓冲区
     */
    void tryClean() {
        //下个周期依旧处于空闲则触发回收任务
        if (!idle) {
            idle = true;
        } else if (!cleanMemoryUnits.isEmpty() && lock.tryLock()) {
            try {
                MemoryUnit cleanBuffer;
                while ((cleanBuffer = cleanMemoryUnits.poll()) != null) {
                    clean0(cleanBuffer);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 回收虚拟缓冲区
     *
     * @param cleanBuffer 虚拟缓冲区
     */
    private void clean0(MemoryUnit cleanBuffer) {
        ListIterator<MemoryUnit> iterator = availableMemoryUnits.listIterator(0);
        while (iterator.hasNext()) {
            MemoryUnit freeBuffer = iterator.next();
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
                    MemoryUnit next = iterator.next();
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
        return "MemoryBlock{availableMemoryUnits=" + availableMemoryUnits + ", cleanMemoryUnits=" + cleanMemoryUnits + '}';
    }
}
