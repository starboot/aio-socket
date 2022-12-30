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
package cn.starboot.http.common.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class RunLogger implements cn.starboot.http.common.logging.Logger {

    private final String loggerName;

    private final Logger logger;

    RunLogger(String name) {
        this.loggerName = name;
        logger = Logger.getLogger(name);
        init();
    }

    private void init() {
        logger.setUseParentHandlers(false);
        try {
            // 移除已注册的Handler
            Handler[] handlers = logger.getHandlers();
            if (handlers != null) {
                for (Handler h : handlers) {
                    logger.removeHandler(h);
                }
            }

            logger.setLevel(Level.ALL);


            // 设置控制台日志Handler
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(new LogFormatter());
            ch.setLevel(Level.ALL);
            try {
                ch.setEncoding("utf8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.addHandler(ch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(Level level, String msg, Object... arguments) {
        LogRecord record = new LogRecord(level, null);
        record.setMessage(msg);

        if (arguments != null && arguments.length > 0 && arguments[arguments.length - 1] instanceof Throwable) {
            record.setThrown((Throwable) arguments[arguments.length - 1]);
        }
        logger.log(record);
    }

    @Override
    public String getName() {
        return loggerName;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {
        log(Level.FINE, msg);
    }

    @Override
    public void trace(String format, Object arg) {
        log(Level.FINE, format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log(Level.FINE, format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        log(Level.FINE, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        log(Level.FINE, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        log(Level.CONFIG, msg);
    }

    @Override
    public void debug(String format, Object arg) {
        log(Level.CONFIG, format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log(Level.CONFIG, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        log(Level.CONFIG, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log(Level.CONFIG, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        log(Level.INFO, msg);
    }

    @Override
    public void info(String format, Object arg) {
        log(Level.INFO, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log(Level.INFO, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        log(Level.INFO, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        log(Level.INFO, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg) {
        log(Level.WARNING, msg);
    }

    @Override
    public void warn(String format, Object arg) {
        log(Level.WARNING, format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        log(Level.WARNING, format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log(Level.WARNING, format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log(Level.WARNING, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String msg) {
        log(Level.SEVERE, msg);
    }

    @Override
    public void error(String format, Object arg) {
        log(Level.SEVERE, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log(Level.SEVERE, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        log(Level.SEVERE, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log(Level.SEVERE, msg, t);
    }
}
