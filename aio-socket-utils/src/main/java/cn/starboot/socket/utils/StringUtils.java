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
package cn.starboot.socket.utils;

/**
 * 扩展StringUtils方法
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class StringUtils {

    public static final String SECRET_KEY = "_SecretKey_";
    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final int TWO = 2;

    public static String toHex(byte b) {
        final char[] buf = new char[TWO];
        for (int i = 0; i < buf.length; i++) {
            buf[1 - i] = DIGITS[b & 0xF];
            b = (byte) (b >>> 4);
        }
        return new String(buf);
    }

    public static String toHexString(final byte[] bytes) {
        final StringBuilder buffer = new StringBuilder(bytes.length);
        buffer.append("\r\n\t\t   0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f\r\n");
        int startIndex = 0;
        int column = 0;
        for (int i = 0; i < bytes.length; i++) {
            column = i % 16;
            switch (column) {
                case 0:
                    startIndex = i;
                    buffer.append(fixHexString(Integer.toHexString(i), 8)).append(": ");
                    buffer.append(toHex(bytes[i]));
                    buffer.append(' ');
                    break;
                case 15:
                    buffer.append(toHex(bytes[i]));
                    buffer.append(" ; ");
                    buffer.append(filterString(bytes, startIndex, column + 1));
                    buffer.append("\r\n");
                    break;
                default:
                    buffer.append(toHex(bytes[i]));
                    buffer.append(' ');
            }
        }

        if (column != 15) {
            for (int i = 0; i < 15 - column; i++) {
                buffer.append("   ");
            }
            buffer.append("; ").append(filterString(bytes, startIndex, column + 1));
            buffer.append("\r\n");
        }

        return buffer.toString();
    }

    private static String filterString(final byte[] bytes, final int offset, final int count) {
        final byte[] buffer = new byte[count];
        System.arraycopy(bytes, offset, buffer, 0, count);
        for (int i = 0; i < count; i++) {
            if (buffer[i] >= 0x0 && buffer[i] <= 0x1F) {
                buffer[i] = 0x2e;
            }
        }
        return new String(buffer);
    }

    private static String fixHexString(final String hexStr, final int length) {
        if (hexStr == null || hexStr.length() == 0) {
            return "00000000h";
        } else {
            final StringBuilder buf = new StringBuilder(length);
            final int strLen = hexStr.length();
            for (int i = 0; i < length - strLen; i++) {
                buf.append('0');
            }
            buf.append(hexStr).append('h');
            return buf.toString();
        }
    }

	public static boolean isBlank(CharSequence str) {
		int length;
		if (str != null && (length = str.length()) != 0) {
			for(int i = 0; i < length; ++i) {
				if (!isBlankChar(str.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public static boolean isBlankChar(char c) {
		return isBlankChar((int)c);
	}

	public static boolean isBlankChar(int c) {
		return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234 || c == 0 || c == 12644 || c == 10240 || c == 6158;
	}

}
