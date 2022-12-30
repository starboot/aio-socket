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
package cn.starboot.http.server.impl;

import cn.starboot.http.common.WriteCache;
import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HeaderValueEnum;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.common.utils.TimerUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
final class HttpOutputStream extends AbstractOutputStream {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    private static final int CACHE_LIMIT = 512;
    /**
     * key:status+contentType
     */
    private static final Map<String, WriteCache>[] CACHE_CONTENT_TYPE_AND_LENGTH = new Map[CACHE_LIMIT];
    private static final Date currentDate = new Date(0);
    private static final Semaphore flushDateSemaphore = new Semaphore(1);
    private static long expireTime;
    private static byte[] dateBytes;
    private static String date;

    static {
        flushDate();
        for (int i = 0; i < CACHE_LIMIT; i++) {
            CACHE_CONTENT_TYPE_AND_LENGTH[i] = new ConcurrentHashMap<>();
        }
    }

    public HttpOutputStream(HttpRequestImpl httpRequest, HttpResponseImpl response, HttpRequestPacket HTTPRequestPacket) {
        super(httpRequest, response, HTTPRequestPacket);
    }

    private static long flushDate() {
        long currentTime = TimerUtils.currentTimeMillis();
        if (currentTime > expireTime && flushDateSemaphore.tryAcquire()) {
            try {
                expireTime = currentTime + 1000;
                currentDate.setTime(currentTime);
                date = sdf.format(currentDate);
                dateBytes = date.getBytes();
            } finally {
                flushDateSemaphore.release();
            }
        }
        return currentTime;
    }

    protected byte[] getHeadPart(boolean hasHeader) {
        long currentTime = flushDate();
        int contentLength = response.getContentLength();
        String contentType = response.getContentType();
        //成功消息优先从缓存中加载。启用缓存的条件：Http_200, contentLength<512,未设置过Header,Http/1.1
        boolean cache = response.isDefaultStatus() && contentLength > 0 && contentLength < CACHE_LIMIT && !hasHeader;

        if (cache) {
            WriteCache data = CACHE_CONTENT_TYPE_AND_LENGTH[contentLength].get(contentType);
            if (data != null) {
                if (currentTime > data.getExpireTime() && data.getSemaphore().tryAcquire()) {
                    try {
                        data.setExpireTime(currentTime + 1000);
                        System.arraycopy(dateBytes, 0, data.getCacheData(), data.getCacheData().length - 4 - dateBytes.length, dateBytes.length);
                    } finally {
                        data.getSemaphore().release();
                    }
                }
                return data.getCacheData();
            }
        }

        StringBuilder sb = new StringBuilder(256);
        sb.append(request.getProtocol()).append(Constant.SP_CHAR).append(response.getHttpStatus()).append(Constant.SP_CHAR).append(response.getReasonPhrase()).append(Constant.CRLF);
        if (contentType != null) {
            sb.append(HeaderNameEnum.CONTENT_TYPE.getName()).append(Constant.COLON_CHAR).append(contentType).append(Constant.CRLF);
        }
        if (contentLength >= 0) {
            sb.append(HeaderNameEnum.CONTENT_LENGTH.getName()).append(Constant.COLON_CHAR).append(contentLength).append(Constant.CRLF);
        } else if (chunked) {
            sb.append(HeaderNameEnum.TRANSFER_ENCODING.getName()).append(Constant.COLON_CHAR).append(HeaderValueEnum.CHUNKED.getName()).append(Constant.CRLF);
        }

        if (configuration.serverName() != null && response.getHeader(HeaderNameEnum.SERVER.getName()) == null) {
            sb.append(SERVER_LINE);
        }
        sb.append(HeaderNameEnum.DATE.getName()).append(Constant.COLON_CHAR).append(date).append(Constant.CRLF);

        //缓存响应头
        if (cache) {
            sb.append(Constant.CRLF);
            WriteCache writeCache = new WriteCache(currentTime + 1000, sb.toString().getBytes());
            CACHE_CONTENT_TYPE_AND_LENGTH[contentLength].put(contentType, writeCache);
            return writeCache.getCacheData();
        }
        return hasHeader ? sb.toString().getBytes() : sb.append(Constant.CRLF).toString().getBytes();
    }
}
