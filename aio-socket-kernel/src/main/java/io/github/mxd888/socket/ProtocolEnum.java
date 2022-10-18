package io.github.mxd888.socket;

/**
 * 协议枚举类
 */
public enum ProtocolEnum {

    /**
     * HTTP 协议
     */
    HTTP(1000, "http protocol"),

    /**
     * bytes 协议
     */
    BYTES(1001, "bytes protocol"),

    /**
     * STRING 协议
     */
    STRING(1002, "string protocol"),

    /**
     * base64 协议
     */
    BASE64(1003, "bases64 protocol"),

    /**
     * Protobuf 协议
     */
    PROTOBUF(1004, "protobuf protocol"),

    // ---------------------以下是留给用户的私有化TCP协议枚举类，共五个---------------------

    PRIVATE_TCP(2000, "private TCP protocol"),

    PRIVATE_TCP_1(2001, "private TCP protocol"),

    PRIVATE_TCP_2(2002, "private TCP protocol"),

    PRIVATE_TCP_3(2003, "private TCP protocol"),

    PRIVATE_TCP_4(2004, "private TCP protocol")
    ;

    private final int code;

    private final String msg;

    ProtocolEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getString() {
        return msg;
    }

}
