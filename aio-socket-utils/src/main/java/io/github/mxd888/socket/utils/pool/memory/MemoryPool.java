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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ByteBuffer内存池
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class MemoryPool {

    /**
     * 守护线程在空闲时期回收内存资源
     */
    private static final ScheduledThreadPoolExecutor BUFFER_POOL_CLEAN = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r, "BufferPoolClean");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * 内存页游标
     */
    private final AtomicInteger cursor = new AtomicInteger(0);

    /**
     * 内存页组
     */
    private MemoryBlock[] memoryBlocks;

    /**
     * 定义内存页池可用状态
     */
    private boolean enabled = true;

    /**
     * 构造内存池
     *
     * @param memoryBlockSize 内存页大小
     * @param memoryBlockNum  内存页个数
     * @param isDirect        是否使用直接缓冲区
     */
    public MemoryPool(final int memoryBlockSize, final int memoryBlockNum, final boolean isDirect) {
        memoryBlocks = new MemoryBlock[memoryBlockNum];
        for (int i = 0; i < memoryBlockNum; i++) {
            memoryBlocks[i] = new MemoryBlock(memoryBlockSize, isDirect);
        }
        if (memoryBlockNum == 0 || memoryBlockSize == 0) {
            future.cancel(false);
        }
    }

    /**
     * 申请内存页
     *
     * @return 缓存页对象
     */
    public MemoryBlock allocateBufferPage() {
        assertEnabled();
        //轮训游标，均衡分配内存页
        return memoryBlocks[(cursor.getAndIncrement() & Integer.MAX_VALUE) % memoryBlocks.length];
    }

    /**
     * 检查内存池状态
     */
    private void assertEnabled() {
        if (!enabled) {
            throw new IllegalStateException("buffer pool is disable");
        }
    }

    /**
     * 释放回收内存
     */
    public void release() {
        enabled = false;
    }

    /**
     * 内存回收任务
     */
    private final ScheduledFuture<?> future = BUFFER_POOL_CLEAN.scheduleWithFixedDelay(new Runnable() {
        @Override
        public void run() {
            if (enabled) {
                for (MemoryBlock memoryBlock : memoryBlocks) {
                    memoryBlock.tryClean();
                }
            } else {
                if (memoryBlocks != null) {
                    for (MemoryBlock page : memoryBlocks) {
                        page.release();
                    }
                    memoryBlocks = null;
                }
                future.cancel(false);
            }
        }
    }, 500, 1000, TimeUnit.MILLISECONDS);

}

