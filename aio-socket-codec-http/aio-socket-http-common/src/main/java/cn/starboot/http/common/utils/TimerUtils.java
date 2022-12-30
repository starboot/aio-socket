package cn.starboot.http.common.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class TimerUtils {

    /**
     * 当前时间
     */
    private static long currentTimeMillis = System.currentTimeMillis();

    static {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "timer");
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleAtFixedRate(() -> currentTimeMillis = System.currentTimeMillis(), 0, 1, TimeUnit.SECONDS);
    }

    public static long currentTimeMillis() {
        return currentTimeMillis;
    }
}
