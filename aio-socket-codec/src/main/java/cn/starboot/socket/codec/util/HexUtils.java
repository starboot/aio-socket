package cn.starboot.socket.codec.util;

import cn.starboot.socket.core.utils.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HexUtils {
	public static final Charset DEFAULT_CHARSET;
	private static final byte[] DIGITS_LOWER;
	private static final byte[] DIGITS_UPPER;
	private static final int OFFSET_BASIS = -2128831035;
	private static final int PRIME = 16777619;

	public HexUtils() {
	}

	public static int hashFNV1(byte[] src) {
		return hashFNV1(src, 0, src.length);
	}

	public static int hashFNV1(byte[] src, int start, int len) {
		int hash = -2128831035;
		int end = start + len;

		for(int i = start; i < end; ++i) {
			hash = (hash ^ src[i]) * 16777619;
		}

		return hash;
	}

	public static byte[] encode(byte[] data) {
		return encode(data, true);
	}

	public static byte[] encode(byte[] data, boolean toLowerCase) {
		return encode(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	public static String encode(byte data) {
		byte[] out = new byte[]{DIGITS_LOWER[(240 & data) >>> 4], DIGITS_LOWER[15 & data]};
		return new String(out);
	}

	private static byte[] encode(byte[] data, byte[] digits) {
		int len = data.length;
		byte[] out = new byte[len << 1];
		int i = 0;

		for(int var5 = 0; i < len; ++i) {
			out[var5++] = digits[(240 & data[i]) >>> 4];
			out[var5++] = digits[15 & data[i]];
		}

		return out;
	}

	public static String encodeToString(byte[] data, boolean toLowerCase) {
		return new String(encode(data, toLowerCase), DEFAULT_CHARSET);
	}

	public static String encodeToString(byte[] data) {
		return encodeToString(data, DEFAULT_CHARSET);
	}

	public static String encodeToString(byte[] data, Charset charset) {
		return new String(encode(data), charset);
	}

	public static String encodeToString(String data) {
		return StringUtils.isBlank(data) ? null : encodeToString(data.getBytes(DEFAULT_CHARSET));
	}

	public static byte[] decode(String data) {
		return decode(data, DEFAULT_CHARSET);
	}

	public static byte[] decode(String data, Charset charset) {
		return StringUtils.isBlank(data) ? null : decode(data.getBytes(charset));
	}

	public static String decodeToString(byte[] data) {
		byte[] decodeBytes = decode(data);
		return new String(decodeBytes, DEFAULT_CHARSET);
	}

	public static String decodeToString(String data) {
		return StringUtils.isBlank(data) ? null : decodeToString(data.getBytes(DEFAULT_CHARSET));
	}

	public static byte[] decode(byte[] data) {
		int len = data.length;
		if ((len & 1) != 0) {
			throw new IllegalArgumentException("hexBinary needs to be even-length: " + len);
		} else {
			byte[] out = new byte[len >> 1];
			int i = 0;

			for(int j = 0; j < len; ++i) {
				int f = toDigit(data[j], j) << 4;
				++j;
				f |= toDigit(data[j], j);
				++j;
				out[i] = (byte)(f & 255);
			}

			return out;
		}
	}

	private static int toDigit(byte b, int index) {
		int digit = Character.digit(b, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("Illegal hexadecimal byte " + b + " at index " + index);
		} else {
			return digit;
		}
	}

	static {
		DEFAULT_CHARSET = StandardCharsets.UTF_8;
		DIGITS_LOWER = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
		DIGITS_UPPER = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
	}
}
