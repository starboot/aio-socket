package io.github.mxd888.socket.utils;



import java.util.concurrent.*;

/**
 * 构造线程池执行器
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
    public static final int MAX_POOL_SIZE_FOR_GROUP	= Integer.getInteger("AIO_MAX_POOL_SIZE_FOR_GROUP", Math.max(CORE_POOL_SIZE * 16, 256));

    /**
     * 保持活跃时间
     */
    public static final long KEEP_ALIVE_TIME = 60L;

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
}
