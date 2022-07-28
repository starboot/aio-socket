package io.github.mxd888.socket.utils;



import java.util.concurrent.*;

/**
 * 线程工具包
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
     * 群组线程池执行器
     */
    private static ThreadPoolExecutor groupExecutor = null;

    /**
     * 构造线程池执行器
     *
     * @return ExecutorService 线程池执行器
     */
    public static ThreadPoolExecutor getGroupExecutor() {
        if (groupExecutor != null) {
            return groupExecutor;
        }
        synchronized (ThreadUtils.class) {
            if (groupExecutor != null) {
                return groupExecutor;
            }
            LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<>();
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
            groupExecutor = new ThreadPoolExecutor(MAX_POOL_SIZE_FOR_GROUP, MAX_POOL_SIZE_FOR_GROUP, KEEP_ALIVE_TIME, TimeUnit.SECONDS, runnableQueue, threadFactory);
            groupExecutor.prestartCoreThread();
            return groupExecutor;
        }
    }
}
