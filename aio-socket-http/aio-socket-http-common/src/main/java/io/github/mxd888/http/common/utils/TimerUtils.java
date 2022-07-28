
package io.github.mxd888.http.common.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2022/2/4
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
