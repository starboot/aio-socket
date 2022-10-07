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
package io.github.mxd888.socket.utils.pool.thread;

import java.util.concurrent.*;

/**
 * 并发同步线程池
 */
public class SynThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * @param corePoolSize             核心池大小
     * @param maximumPoolSize          池最大值
     * @param keepAliveTime            单位: 秒
     * @param runnableQueue            阻塞队列
     * @param threadFactory            线程工厂
     * @param rejectedExecutionHandler 拒绝策略
     */
    public SynThreadPoolExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit timeUnit,
                                 BlockingQueue<Runnable> runnableQueue,
                                 ThreadFactory threadFactory,
                                 RejectedExecutionHandler rejectedExecutionHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, runnableQueue, threadFactory, rejectedExecutionHandler);
    }

    /**
     * 执行前检查是否为同步任务
     *
     * @param runnable 任务
     * @return 检查状态
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
            }
        } else {
            super.execute(runnable);
        }

    }

    @Override
    public <R> Future<R> submit(Runnable runnable, R result) {
        if (checkBeforeExecute(runnable)) {
            return super.submit(runnable, result);
        } else {
            return null;
        }
    }

}
