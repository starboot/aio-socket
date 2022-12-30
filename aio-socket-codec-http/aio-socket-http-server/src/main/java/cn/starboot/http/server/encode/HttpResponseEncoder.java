package cn.starboot.http.server.encode;

import cn.starboot.http.server.HttpResponse;
import cn.starboot.http.server.impl.HttpResponsePacket;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.http.common.AbstractResponseEncoder;
import cn.starboot.http.common.Cookie;
import cn.starboot.http.common.HeaderValue;
import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.common.utils.GzipUtils;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HTTP协议编码
 */
public class HttpResponseEncoder extends AbstractResponseEncoder {

	private static final Map<String, byte[]> HEADER_NAME_EXT_MAP = new ConcurrentHashMap<>();

	// HTTP编码
	public void encode(HttpResponsePacket httpResponsePacket, ChannelContext channelContext) throws AioEncoderException {
		WriteBuffer writeBuffer = channelContext.getWriteBuffer();
		boolean chunked = httpResponsePacket.isChunked();
		boolean directWrite = httpResponsePacket.isDirectWrite();
		boolean closed = httpResponsePacket.isClosed();
		if (!directWrite) {
			// 输出头部信息
			writeHeader(httpResponsePacket, writeBuffer);
		}
		// 判断是否为关闭报文
		if (closed) {
			if (chunked) {
				writeBuffer.write(Constant.CHUNKED_END_BYTES);
			}
			return;
		}
		byte[] data = httpResponsePacket.getData();
		if (data == null) {
			return;
		}
		int off = 0;
		int len = data.length;
		if (httpResponsePacket.isChunked()) {
			if (httpResponsePacket.isGzip()) {
				data = GzipUtils.compress(data, off, len);
				off = 0;
				len = data.length;
			}
			byte[] start = getBytes(Integer.toHexString(len) + "\r\n");
			writeBuffer.write(start);
			writeBuffer.write(data, off, len);
			writeBuffer.write(Constant.CRLF_BYTES);
		} else {
			writeBuffer.write(data, off, len);
		}

	}

	/**
	 * 输出Http消息头
	 */
	protected void writeHeader(HttpResponsePacket httpResponsePacket, WriteBuffer writeBuffer) throws AioEncoderException {
		if (httpResponsePacket.isCommitted()) {
			return;
		}
		//转换Cookie
		convertCookieToHeader(httpResponsePacket.getResponse());

		//输出http状态行、contentType,contentLength、Transfer-Encoding、server等信息
		writeBuffer.write(httpResponsePacket.getHeadPart());
		if (httpResponsePacket.isHasHeader()) {
			//输出Header部分
			writeHeaders(httpResponsePacket.getResponse(), writeBuffer);
		}
		httpResponsePacket.setCommitted(true);
	}


	private void convertCookieToHeader(HttpResponse response) {
		List<Cookie> cookies = response.getCookies();
		if (cookies.size() > 0) {
			cookies.forEach(cookie -> response.addHeader(HeaderNameEnum.SET_COOKIE.getName(), cookie.toString()));
		}
	}


	private void writeHeaders(HttpResponse response, WriteBuffer writeBuffer) throws AioEncoderException {
		for (Map.Entry<String, HeaderValue> entry : response.getHeaders().entrySet()) {
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

	protected final byte[] getHeaderNameBytes(String name) {
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

	protected final byte[] getBytes(String str) {
		return str.getBytes(StandardCharsets.US_ASCII);
	}
}
