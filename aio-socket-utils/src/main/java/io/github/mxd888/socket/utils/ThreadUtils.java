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
package io.github.mxd888.socket.utils;

import io.github.mxd888.socket.utils.pool.thread.AioCallerRunsPolicy;
import io.github.mxd888.socket.utils.pool.thread.DefaultThreadFactory;
import io.github.mxd888.socket.utils.pool.thread.SynThreadPoolExecutor;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * 构造线程池执行器（异步线程池/同步线程池）
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ThreadUtils {

    /**
     * 电脑处理器数量
     */
    public static final int	AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 核心池大小
     */
    public static final int CORE_POOL_SIZE = AVAILABLE_PROCESSORS;

    /**
     * 最大内存池线程数
     */
    public static final int MAX_POOL_SIZE_FOR_GROUP	= Integer.getInteger("AIO_MAX_POOL_SIZE_FOR_GROUP", Math.max(CORE_POOL_SIZE * 5, 10));

    /**
     * 保持活跃时间
     */
    public static final long KEEP_ALIVE_TIME = 0L;

    /**
     * 构造线程池执行器
     *
     * @return ExecutorService 线程池执行器
     */
    public static ThreadPoolExecutor getGroupExecutor() {
        return getGroupExecutor(MAX_POOL_SIZE_FOR_GROUP);
    }

    public static ThreadPoolExecutor getGroupExecutor(int threadNum) {
        return getGroupExecutor(threadNum, KEEP_ALIVE_TIME, TimeUnit.SECONDS);
    }

    public static ThreadPoolExecutor getGroupExecutor(int threadNum,
                                                      long keepAliveTime,
                                                      TimeUnit timeUnit) {
        LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>();
        return getGroupExecutor(threadNum, keepAliveTime, timeUnit, linkedBlockingQueue);
    }

    public static ThreadPoolExecutor getGroupExecutor(int threadNum,
                                                      long keepAliveTime,
                                                      TimeUnit timeUnit,
                                                      BlockingQueue<Runnable> workQueue) {
        return getGroupExecutor(threadNum, keepAliveTime, timeUnit, workQueue, Executors.defaultThreadFactory());
    }

    public static ThreadPoolExecutor getGroupExecutor(int threadNum,
                                                      long keepAliveTime,
                                                      TimeUnit timeUnit,
                                                      BlockingQueue<Runnable> workQueue,
                                                      ThreadFactory threadFactory) {
        ThreadPoolExecutor groupExecutor = new ThreadPoolExecutor(threadNum, threadNum, keepAliveTime, timeUnit, workQueue, threadFactory);
        groupExecutor.prestartCoreThread();
        return groupExecutor;
    }

    private static SynThreadPoolExecutor aioExecutor;

    /**
     * aio-socket-kernel 框架使用的先来先服务线程池
     *
     * @return           内核线程池
     */
    public static SynThreadPoolExecutor getAioExecutor() {
        if (aioExecutor != null) {
            return aioExecutor;
        }
        synchronized (ThreadUtils.class) {
            if (aioExecutor != null) {
                return aioExecutor;
            }
            LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<>();
            String threadName = "aio-worker";
            DefaultThreadFactory defaultThreadFactory = DefaultThreadFactory.getInstance(threadName, Thread.MAX_PRIORITY);
            ThreadPoolExecutor.CallerRunsPolicy callerRunsPolicy = new AioCallerRunsPolicy();
            aioExecutor = getAioExecutor(MAX_POOL_SIZE_FOR_GROUP * 3, MAX_POOL_SIZE_FOR_GROUP * 3, KEEP_ALIVE_TIME, runnableQueue, defaultThreadFactory, callerRunsPolicy, threadName);
            aioExecutor.prestartCoreThread();
            return aioExecutor;
        }
    }

    /**
     * 留给其他用户调用的先来先服务线程池
     *
     * @param corePoolSize      核心线程数
     * @param maximumPoolSize   最大线程数
     * @param keepAliveTime     存活时间
     * @return                  同步线程池
     */
    public static SynThreadPoolExecutor getAioExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        return getAioExecutor(corePoolSize, maximumPoolSize, keepAliveTime, new LinkedBlockingQueue<>());
    }

    public static SynThreadPoolExecutor getAioExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue) {
        return getAioExecutor(corePoolSize, maximumPoolSize, keepAliveTime, runnableQueue, Executors.defaultThreadFactory());
    }

    public static SynThreadPoolExecutor getAioExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory) {
        return getAioExecutor(corePoolSize, maximumPoolSize, keepAliveTime, runnableQueue, threadFactory, new AioCallerRunsPolicy());
    }

    public static SynThreadPoolExecutor getAioExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return getAioExecutor(corePoolSize, maximumPoolSize, keepAliveTime, runnableQueue, threadFactory, rejectedExecutionHandler, "aio-worker-" + UUID.randomUUID());
    }

    public static SynThreadPoolExecutor getAioExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler, String name) {
        SynThreadPoolExecutor poolExecutor = new SynThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, runnableQueue, threadFactory, name, rejectedExecutionHandler);
        poolExecutor.prestartCoreThread();
        return poolExecutor;
    }

}
