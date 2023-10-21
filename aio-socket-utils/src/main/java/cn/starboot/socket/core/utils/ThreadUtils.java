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
package cn.starboot.socket.core.utils;

import cn.starboot.socket.core.utils.pool.thread.AioCallerRunsPolicy;
import cn.starboot.socket.core.utils.pool.thread.AioThreadPoolExecutor;
import cn.starboot.socket.core.utils.pool.thread.DefaultThreadFactory;

import java.util.concurrent.*;

/**
 * 构造线程池执行器（异步线程池/同步线程池）
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ThreadUtils {

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
    public static final int MAX_POOL_SIZE_FOR_BOSS = Math.max(CORE_POOL_SIZE, 2);

    /**
     * worker 最大内存池线程数
     */
    public static final int MAX_POOL_SIZE_FOR_AIO_WORKER = MAX_POOL_SIZE_FOR_BOSS * 4;

    /**
     * 构造线程池执行器
     * 用户调用getGroupExecutor()所返回的线程池是aio-socket框架在启动的时候已经创建完毕的;
     * 若用户想创建新的、独立的ExecutorService请调用其他带有参数的方法
     * 推荐使用getGroupExecutor()，和框架共用线程池会节省服务器资源
     *
     * @return ExecutorService 线程池执行器
     */
    public static ExecutorService getGroupExecutor() {
        return getGroupExecutor(MAX_POOL_SIZE_FOR_BOSS);
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

    public static ExecutorService getAioExecutor() {
        return getAioExecutor(MAX_POOL_SIZE_FOR_AIO_WORKER);
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
        AioThreadPoolExecutor poolExecutor =
                new AioThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit,
                        runnableQueue, threadFactory, rejectedExecutionHandler);
        poolExecutor.prestartCoreThread();
        return poolExecutor;
    }

    private ThreadUtils() {
    }

}
