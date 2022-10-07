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

import java.util.concurrent.*;

/**
 * 构造线程池执行器（异步线程池/同步线程池）
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ThreadUtils {

    /**
     * aio-socket ThreadPool
     */
    private static ExecutorService aioExecutor;

    /**
     * Java8 ThreadPool
     */
    private static ExecutorService executorService;

    /**
     * 保持活跃时间
     */
    public static final long KEEP_ALIVE_TIME = 0L;

    /**
     * ThreadName
     */
    private static final String defaultThreadName = "aio-worker";

    /**
     * 电脑处理器数量
     */
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 核心池大小
     */
    public static final int CORE_POOL_SIZE = AVAILABLE_PROCESSORS;

    /**
     * boss 最大内存池线程数
     */
    public static final int MAX_POOL_SIZE_FOR_JAVA = Math.max(CORE_POOL_SIZE * 5, 10);

    /**
     * worker 最大内存池线程数
     */
    public static final int MAX_POOL_SIZE_FOR_AIO = MAX_POOL_SIZE_FOR_JAVA * 3;

    /**
     * 构造线程池执行器
     * 用户调用getGroupExecutor()所返回的线程池是aio-socket框架在启动的时候已经创建完毕的;
     * 若用户想创建新的、独立的ExecutorService请调用其他带有参数的方法
     * 推荐使用getGroupExecutor()，和框架共用线程池会节省服务器资源
     *
     * @return ExecutorService 线程池执行器
     */
    public static ExecutorService getGroupExecutor() {
        if (executorService != null) {
            return executorService;
        }
        synchronized (ThreadUtils.class) {
            if (executorService != null) {
                return executorService;
            }
            executorService = getGroupExecutor(MAX_POOL_SIZE_FOR_JAVA);
        }
        return executorService;
    }

    public static ExecutorService getGroupExecutor(int corePoolSize) {
        return getGroupExecutor(corePoolSize, corePoolSize);
    }

    public static ExecutorService getGroupExecutor(int corePoolSize, int maxPoolSize) {
        return getGroupExecutor(corePoolSize, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS);
    }

    public static ExecutorService getGroupExecutor(int corePoolSize,
                                                   int maxPoolSize,
                                                   long keepAliveTime,
                                                   TimeUnit timeUnit) {
        LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>();
        return getGroupExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, linkedBlockingQueue);
    }

    public static ExecutorService getGroupExecutor(int corePoolSize,
                                                   int maxPoolSize,
                                                   long keepAliveTime,
                                                   TimeUnit timeUnit,
                                                   BlockingQueue<Runnable> workQueue) {
        return getGroupExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                timeUnit, workQueue, Executors.defaultThreadFactory());
    }

    public static ExecutorService getGroupExecutor(int corePoolSize,
                                                   int maxPoolSize,
                                                   long keepAliveTime,
                                                   TimeUnit timeUnit,
                                                   BlockingQueue<Runnable> workQueue,
                                                   ThreadFactory threadFactory) {
        return getGroupExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                timeUnit, workQueue, threadFactory, new AioCallerRunsPolicy());
    }

    public static ExecutorService getGroupExecutor(int corePoolSize,
                                                   int maxPoolSize,
                                                   long keepAliveTime,
                                                   TimeUnit timeUnit,
                                                   BlockingQueue<Runnable> workQueue,
                                                   ThreadFactory threadFactory,
                                                   RejectedExecutionHandler rejectedExecutionHandler) {
        ThreadPoolExecutor groupExecutor =
                new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                        timeUnit, workQueue, threadFactory, rejectedExecutionHandler);
        groupExecutor.prestartCoreThread();
        return groupExecutor;
    }

    /**
     * aio-socket-kernel 框架使用的先来先服务线程池;
     * 用户调用getAioExecutor()所返回的线程池是aio-socket框架在启动的时候已经创建完毕的;
     * 若用户想创建新的、独立的AioExecutor请调用其他带有参数的方法
     * 推荐使用getAioExecutor()，和框架共用线程池会节省服务器资源
     *
     * @return 内核线程池
     */
    public static ExecutorService getAioExecutor() {
        if (aioExecutor != null) {
            return aioExecutor;
        }
        synchronized (ThreadUtils.class) {
            if (aioExecutor != null) {
                return aioExecutor;
            }
            aioExecutor = getAioExecutor(MAX_POOL_SIZE_FOR_AIO);
            return aioExecutor;
        }
    }

    public static ExecutorService getAioExecutor(int corePoolSize) {
        return getAioExecutor(corePoolSize, corePoolSize);
    }

    public static ExecutorService getAioExecutor(int corePoolSize,
                                                 int maxPoolSize) {
        return getAioExecutor(corePoolSize, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS);
    }

    public static ExecutorService getAioExecutor(int corePoolSize,
                                                 int maxPoolSize,
                                                 long keepAliveTime,
                                                 TimeUnit timeUnit) {
        return getAioExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                new LinkedBlockingQueue<>());
    }

    public static ExecutorService getAioExecutor(int corePoolSize,
                                                 int maxPoolSize,
                                                 long keepAliveTime,
                                                 TimeUnit timeUnit,
                                                 BlockingQueue<Runnable> runnableQueue) {
        return getAioExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                runnableQueue, DefaultThreadFactory.getInstance(defaultThreadName, Thread.MAX_PRIORITY));
    }

    public static ExecutorService getAioExecutor(int corePoolSize,
                                                 int maxPoolSize,
                                                 long keepAliveTime,
                                                 TimeUnit timeUnit,
                                                 BlockingQueue<Runnable> runnableQueue,
                                                 ThreadFactory threadFactory) {
        return getAioExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                runnableQueue, threadFactory, new AioCallerRunsPolicy());
    }

    public static ExecutorService getAioExecutor(int corePoolSize,
                                                 int maxPoolSize,
                                                 long keepAliveTime,
                                                 TimeUnit timeUnit,
                                                 BlockingQueue<Runnable> runnableQueue,
                                                 ThreadFactory threadFactory,
                                                 RejectedExecutionHandler rejectedExecutionHandler) {
        SynThreadPoolExecutor poolExecutor =
                new SynThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                        runnableQueue, threadFactory, rejectedExecutionHandler);
        poolExecutor.prestartCoreThread();
        return poolExecutor;
    }

    private ThreadUtils() {
    }

}
