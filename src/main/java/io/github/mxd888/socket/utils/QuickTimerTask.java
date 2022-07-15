package io.github.mxd888.socket.utils;

import java.util.concurrent.*;

/**
 * 服务器定时任务
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class QuickTimerTask implements Runnable {
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Quick Timer");
            thread.setDaemon(true);
            return thread;
        }
    });

    public QuickTimerTask() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this, getDelay(), getPeriod(), TimeUnit.MILLISECONDS);
//        logger.info("Register QuickTimerTask---- " + this.getClass().getSimpleName());
    }

    public static void cancelQuickTask() {
        SCHEDULED_EXECUTOR_SERVICE.shutdown();
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period) {
        return SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }


    /**
     * 获取定时任务的延迟启动时间
     */
    protected long getDelay() {
        return 0;
    }

    /**
     * 获取定时任务的执行频率
     */
    protected abstract long getPeriod();
}
