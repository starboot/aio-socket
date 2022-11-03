package io.github.mxd888.http.server.encode;

import io.github.mxd888.http.common.AbstractResponseEncoder;
import io.github.mxd888.http.common.BufferOutputStream;
import io.github.mxd888.http.common.HeaderValue;
import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.enums.HeaderValueEnum;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.TimerUtils;
import io.github.mxd888.http.server.HttpRequest;
import io.github.mxd888.http.server.HttpResponse;
import io.github.mxd888.http.server.HttpResponsePacket;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * HTTP协议编码
 */
public class HttpResponseEncoder extends AbstractResponseEncoder {

	private static final Map<String, byte[]> HEADER_NAME_EXT_MAP = new ConcurrentHashMap<>();
	protected static String SERVER_LINE = null;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	private static final int CACHE_LIMIT = 512;
	/**
	 * key:status+contentType
	 */
	private static final Map<String, BufferOutputStream.WriteCache>[] CACHE_CONTENT_TYPE_AND_LENGTH = new Map[CACHE_LIMIT];
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
		if (SERVER_LINE == null) {
			SERVER_LINE = HeaderNameEnum.SERVER.getName() + Constant.COLON_CHAR + "aio-socket" + Constant.CRLF;
		}
	}


	public static void encode(HttpResponsePacket httpResponse, ChannelContext channelContext) throws IOException {

		//拿到输出流
		WriteBuffer writeBuffer = channelContext.getWriteBuffer();
		boolean hasHeader = hasHeader(httpResponse.getResponse());
		//输出http状态行、contentType,contentLength、Transfer-Encoding、server等信息
		byte[] headPart = getHeadPart(httpResponse.getRequest(), httpResponse.getResponse(), hasHeader);
		writeBuffer.write(headPart);
		//输出Header部分
		if (hasHeader) {
			for (Map.Entry<String, HeaderValue> entry : httpResponse.getResponse().getHeaders().entrySet()) {
				HeaderValue headerValue = entry.getValue();
				while (headerValue != null) {
					writeBuffer.write(getHeaderNameBytes(entry.getKey()));
					writeBuffer.write(getBytes(headerValue.getValue()));
					writeBuffer.write(Constant.CRLF_BYTES);
					headerValue = headerValue.getNextValue();
				}
			}
			writeBuffer.write(Constant.CRLF_BYTES);
		}
		// 输出体
		System.out.println("不chunked");
		writeBuffer.write(httpResponse.getData());

	}

	protected static boolean hasHeader(HttpResponse response) {
		return response.getHeaders().size() > 0;
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

	protected static final byte[] getBytes(String str) {
		return str.getBytes(StandardCharsets.US_ASCII);
	}

	protected static final byte[] getHeaderNameBytes(String name) {
		HeaderNameEnum headerNameEnum = HeaderNameEnum.HEADER_NAME_ENUM_MAP.get(name);
		if (headerNameEnum != null) {
			return headerNameEnum.getBytesWithColon();
		}
		byte[] extBytes = HEADER_NAME_EXT_MAP.get(name);
		if (extBytes == null) {
			synchronized (name) {
				extBytes = getBytes(name + ":");
				HEADER_NAME_EXT_MAP.put(name, extBytes);
			}
		}
		return extBytes;
	}

	protected static byte[] getHeadPart(HttpRequest request, HttpResponse response, boolean hasHeader) {
		long currentTime = flushDate();
		int contentLength = response.getContentLength();
		String contentType = response.getContentType();
		//成功消息优先从缓存中加载。启用缓存的条件：Http_200, contentLength<512,未设置过Header,Http/1.1
		boolean cache = response.isDefaultStatus() && contentLength > 0 && contentLength < CACHE_LIMIT && !hasHeader;

		if (cache) {
			BufferOutputStream.WriteCache data = CACHE_CONTENT_TYPE_AND_LENGTH[contentLength].get(contentType);
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
		} else if (true) {
			sb.append(HeaderNameEnum.TRANSFER_ENCODING.getName()).append(Constant.COLON_CHAR).append(HeaderValueEnum.CHUNKED.getName()).append(Constant.CRLF);
		}

		if ("aio-socket" != null && response.getHeader(HeaderNameEnum.SERVER.getName()) == null) {
			sb.append(SERVER_LINE);
		}
		sb.append(HeaderNameEnum.DATE.getName()).append(Constant.COLON_CHAR).append(date).append(Constant.CRLF);

		//缓存响应头
		if (cache) {
			sb.append(Constant.CRLF);
			BufferOutputStream.WriteCache writeCache = new BufferOutputStream.WriteCache(currentTime + 1000, sb.toString().getBytes());
			CACHE_CONTENT_TYPE_AND_LENGTH[contentLength].put(contentType, writeCache);
			return writeCache.getCacheData();
		}
		return hasHeader ? sb.toString().getBytes() : sb.append(Constant.CRLF).toString().getBytes();
	}
}
