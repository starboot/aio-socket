package io.github.mxd888.http.common.utils;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface Constant {
    public static final int WS_DEFAULT_MAX_FRAME_SIZE = (2 << 16) - 1;
    public static final int WS_PLAY_LOAD_126 = 126;
    public static final int WS_PLAY_LOAD_127 = 127;

    /**
     * Post 最大长度
     */
    int maxPostSize = 2 * 1024 * 1024;

    String SCHEMA_HTTP = "http";
    String SCHEMA_HTTPS = "https";
    /**
     * Horizontal space
     */
    public static final byte SP = 32;

    /**
     * Horizontal tab
     */
    public static final byte HT = 9;

    /**
     * Carriage return
     */
    public static final byte CR = 13;

    /**
     * Equals '='
     */
    public static final byte EQUALS = 61;

    /**
     * Line feed character
     */
    public static final byte LF = 10;

    /**
     * Colon ':'
     */
    public static final byte COLON = 58;

    /**
     * Semicolon ';'
     */
    public static final byte SEMICOLON = 59;

    /**
     * Comma ','
     */
    public static final byte COMMA = 44;

    /**
     * Double quote '"'
     */
    public static final byte DOUBLE_QUOTE = '"';


    /**
     * Horizontal space
     */
    public static final char SP_CHAR = (char) SP;

    char COLON_CHAR = COLON;

    public static final byte[] CRLF_BYTES = {Constant.CR, Constant.LF};

    String CRLF = "\r\n";

    byte[] HEADER_END = {Constant.CR, Constant.LF, Constant.CR, Constant.LF};

    byte[] CHUNKED_END_BYTES = "0\r\n\r\n".getBytes(StandardCharsets.US_ASCII);

}