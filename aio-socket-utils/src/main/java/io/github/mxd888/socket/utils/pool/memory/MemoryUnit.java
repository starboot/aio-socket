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
package io.github.mxd888.socket.utils.pool.memory;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

/**
 * 虚拟ByteBuffer缓冲区
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class MemoryUnit {

    /**
     * 当前虚拟buffer的归属内存页 用来申请物理空间
     */
    private final MemoryBlock memoryBlock;

    /**
     * 通过ByteBuffer.slice()隐射出来的虚拟ByteBuffer
     */
    private ByteBuffer buffer;

    /**
     * 是否已回收
     */
    private final Semaphore clean = new Semaphore(1);

    /**
     * 当前虚拟buffer映射的实际buffer.position
     */
    private int parentPosition;

    /**
     * 当前虚拟buffer映射的实际buffer.limit
     */
    private int parentLimit;

    /**
     * 缓冲区容量
     */
    private int capacity;

    MemoryUnit(MemoryBlock memoryBlock, ByteBuffer buffer, int parentPosition, int parentLimit) {
        this.memoryBlock = memoryBlock;
        this.buffer = buffer;
        this.parentPosition = parentPosition;
        this.parentLimit = parentLimit;
        updateCapacity();
    }

    /**
     * 将ByteBuffer转化为VirtualBuffer类型
     *
     * @param buffer 需要转换的原始buffer
     * @return       虚拟内存类型的数据
     */
    public static MemoryUnit wrap(ByteBuffer buffer) {
        return new MemoryUnit(null, buffer, 0, 0);
    }

    int getParentPosition() {
        return parentPosition;
    }

    void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
        updateCapacity();
    }

    int getParentLimit() {
        return parentLimit;
    }

    void setParentLimit(int parentLimit) {
        this.parentLimit = parentLimit;
        updateCapacity();
    }

    /**
     * 更新虚拟内存容量
     */
    private void updateCapacity() {
        capacity = this.parentLimit - this.parentPosition;
    }

    /**
     * 获取当前容量
     *
     * @return int
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * 获取真实缓冲区
     *
     * @return 真实缓冲区
     */
    public ByteBuffer buffer() {
        return buffer;
    }

    /**
     * 设置真实缓冲区
     *
     * @param buffer 真实缓冲区
     */
    void buffer(ByteBuffer buffer) {
        this.buffer = buffer;
        clean.release();
    }

    /**
     * 释放虚拟缓冲区
     */
    public void clean() {
        if (clean.tryAcquire()) {
            if (memoryBlock != null) {
                memoryBlock.clean(this);
            }
        } else {
            throw new UnsupportedOperationException("buffer has cleaned");
        }
    }

    @Override
    public String toString() {
        return "VirtualBuffer{parentPosition=" + parentPosition + ", parentLimit=" + parentLimit + '}';
    }
}
