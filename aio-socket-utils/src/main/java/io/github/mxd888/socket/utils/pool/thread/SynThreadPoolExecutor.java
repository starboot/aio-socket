package io.github.mxd888.socket.utils.pool.thread;

import java.util.concurrent.*;

public class SynThreadPoolExecutor extends ThreadPoolExecutor {

    /** The name. */
    private String name = null;

    /**
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime 单位: 秒
     * @param runnableQueue
     * @param threadFactory
     * @param name
     * @author tanyaowu
     */
    public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, runnableQueue, threadFactory);
        this.name = name;
    }

    /**
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param runnableQueue
     * @param threadFactory
     * @param name
     * @param rejectedExecutionHandler
     */
    public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name, RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, runnableQueue, threadFactory, rejectedExecutionHandler);
        this.name = name;
    }

    /**
     *
     * @param runnable
     * @return
     * @author tanyaowu
     */
    private boolean checkBeforeExecute(Runnable runnable) {
        if (runnable instanceof AbstractSynRunnable) {
            AbstractSynRunnable synRunnable = (AbstractSynRunnable) runnable;
            if (synRunnable.executed) {
                return false;
            }

            boolean tryLock = synRunnable.runningLock.tryLock();
            if (tryLock) {
                try {
                    if (synRunnable.executed) {
                        return false;
                    }
                    synRunnable.executed = true;
                    return true;
                } finally {
                    synRunnable.runningLock.unlock();
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void execute(Runnable runnable) {
        if (runnable instanceof AbstractSynRunnable) {
            AbstractSynRunnable synRunnable = (AbstractSynRunnable) runnable;
            if (synRunnable.executed) {
                return;
            }

            boolean tryLock = synRunnable.runningLock.tryLock();
            if (tryLock) {
                try {
                    if (synRunnable.executed) {
                        return;
                    }
                    synRunnable.executed = true;
                    super.execute(runnable);
                } finally {
                    synRunnable.runningLock.unlock();
                }
            } else {
                return;
            }
        } else {
            super.execute(runnable);
        }

    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <R> Future<R> submit(Runnable runnable, R result) {
        if (checkBeforeExecute(runnable)) {
            Future<R> ret = super.submit(runnable, result);
            return ret;
        } else {
            return null;
        }
    }

}
