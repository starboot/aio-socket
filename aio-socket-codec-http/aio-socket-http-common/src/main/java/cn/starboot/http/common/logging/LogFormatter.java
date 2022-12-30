package cn.starboot.http.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class LogFormatter extends Formatter {
    private final static String format = "{0,date,yyyy-MM-dd} {0,time,HH:mm:ss.SS}";
    private final String lineSeparator = System.getProperty("line.separator");
    Date dat = new Date();
    String logClassName = RunLogger.class.getName();
    private MessageFormat formatter;
    private Object args[] = new Object[1];

    @Override
    public synchronized String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        sb.append("[").append(text).append("] ["); // 时间

        sb.append(record.getLevel().getName());// 日志级别
        sb.append("] ");

//        sb.append("[Thread-" + record.getThreadID() + "] "); // 线程
        sb.append("[" + Thread.currentThread().getName() + "] "); // 线程

        StackTraceElement[] stackElement = new Throwable().getStackTrace();
        boolean lookingForLogger = true;
        for (StackTraceElement stack : stackElement) {
            String cname = stack.getClassName();
            if (lookingForLogger) {
                // Skip all frames until we have found the first logger frame.
                if (cname.matches(logClassName)) {
                    lookingForLogger = false;
                }
            } else {
                if (!cname.matches(logClassName)) {
                    String simpleClassName = cname.substring(cname
                            .lastIndexOf(".") + 1);
                    sb.append("[" + simpleClassName + "("
                            + stack.getMethodName() + ":"
                            + stack.getLineNumber() + ")]");
                    break;
                }
            }
        }

        String message = formatMessage(record);
        sb.append(message);
        if (record.getThrown() != null) {
            try {
                sb.append(lineSeparator);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
            }
        }
        sb.append(lineSeparator);
        return sb.toString();
    }
}
