
package io.github.mxd888.socket.buffer;

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
public final class BufferPagePool {

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
    private BufferPage[] bufferPages;

    /**
     * 定义内存页池可用状态
     */
    private boolean enabled = true;

    /**
     * 构造内存池
     *
     * @param pageSize 内存页大小
     * @param pageNum  内存页个数
     * @param isDirect 是否使用直接缓冲区
     */
    public BufferPagePool(final int pageSize, final int pageNum, final boolean isDirect) {
        bufferPages = new BufferPage[pageNum];
        for (int i = 0; i < pageNum; i++) {
            bufferPages[i] = new BufferPage(pageSize, isDirect);
        }
        if (pageNum == 0 || pageSize == 0) {
            future.cancel(false);
        }
    }

    /**
     * 申请内存页
     *
     * @return 缓存页对象
     */
    public BufferPage allocateBufferPage() {
        assertEnabled();
        //轮训游标，均衡分配内存页
        return bufferPages[(cursor.getAndIncrement() & Integer.MAX_VALUE) % bufferPages.length];
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
                for (BufferPage bufferPage : bufferPages) {
                    bufferPage.tryClean();
                }
            } else {
                if (bufferPages != null) {
                    for (BufferPage page : bufferPages) {
                        page.release();
                    }
                    bufferPages = null;
                }
                future.cancel(false);
            }
        }
    }, 500, 1000, TimeUnit.MILLISECONDS);


}

