/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class DateUtils {
    private static final String COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
    private static final String LAST_MODIFIED_PATTERN = "E, dd MMM yyyy HH:mm:ss z";
    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(LAST_MODIFIED_PATTERN, Locale.ENGLISH);
        }
    };

    private static final ThreadLocal<SimpleDateFormat> COOKIE_FORMAT = ThreadLocal.withInitial(() -> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COOKIE_PATTERN, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat;
    });

    public static Date parseLastModified(String date) throws ParseException {
        return sdf.get().parse(date);
    }

    public static String formatLastModified(Date date) {
        return sdf.get().format(date);
    }

    public static String formatCookieExpire(Date date) {
        return COOKIE_FORMAT.get().format(date);
    }
}
