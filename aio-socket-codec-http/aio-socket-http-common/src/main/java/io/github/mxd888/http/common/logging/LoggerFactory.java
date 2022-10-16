package io.github.mxd888.http.common.logging;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class LoggerFactory {
    private static final Map<String, Logger> loggerMap = new HashMap<>();

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        Logger logger = loggerMap.get(name);
        if (logger != null) {
            return logger;
        }
        logger = new RunLogger(name);
        loggerMap.put(name, logger);
        return logger;
    }
}
