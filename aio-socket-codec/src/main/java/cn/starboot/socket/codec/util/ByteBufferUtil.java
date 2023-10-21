package cn.starboot.socket.codec.util;

import cn.starboot.socket.core.utils.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ByteBufferUtil {
	public static final byte[] EMPTY_BYTES = new byte[0];

	public ByteBufferUtil() {
	}

	public static byte readByte(ByteBuffer buffer) {
		return buffer.get();
	}

	public static short readUnsignedByte(ByteBuffer buffer) {
		return (short)(buffer.get() & 255);
	}

	public static String readString(ByteBuffer buffer, int count) {
		return readString(buffer, count, StandardCharsets.UTF_8);
	}

	public static String readString(ByteBuffer buffer, int count, Charset charset) {
		byte[] bytes = new byte[count];
		buffer.get(bytes);
		return new String(bytes, charset);
	}

	public static int readUnsignedShort(ByteBuffer buffer) {
		int ch1 = buffer.get() & 255;
		int ch2 = buffer.get() & 255;
		return (ch1 << 8) + ch2;
	}

	public static ByteBuffer skipBytes(ByteBuffer buffer, int skip) {
		buffer.position(buffer.position() + skip);
		return buffer;
	}

	public static String toString(ByteBuffer buffer) {
		return toString(buffer, StandardCharsets.UTF_8);
	}

	public static String toString(ByteBuffer buffer, Charset charset) {
		return new String(buffer.array(), buffer.position(), buffer.limit(), charset);
	}

	public static ByteBuffer clone(ByteBuffer original) {
		ByteBuffer clone = ByteBuffer.allocate(original.capacity());
		original.rewind();
		clone.put(original);
		original.rewind();
		clone.flip();
		return clone;
	}

	public static String hexDump(ByteBuffer byteBuffer) {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return toHexString(bytes);
	}

	public static String toHexString(byte[] bytes) {
		return StringUtils.toHexString(bytes);
	}

	private static String filterString(byte[] bytes, int offset, int count) {
		byte[] buffer = new byte[count];
		System.arraycopy(bytes, offset, buffer, 0, count);

		for(int i = 0; i < count; ++i) {
			if (buffer[i] >= 0 && buffer[i] <= 31) {
				buffer[i] = 46;
			}
		}

		return new String(buffer);
	}

	private static String fixHexString(String hexStr) {
		if (hexStr != null && hexStr.length() != 0) {
			StringBuilder buf = new StringBuilder(8);
			int strLen = hexStr.length();

			for(int i = 0; i < 8 - strLen; ++i) {
				buf.append('0');
			}

			buf.append(hexStr).append('h');
			return buf.toString();
		} else {
			return "00000000h";
		}
	}
}
