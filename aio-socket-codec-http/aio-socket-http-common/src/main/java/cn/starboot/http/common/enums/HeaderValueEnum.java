package cn.starboot.http.common.enums;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum HeaderValueEnum {

    CHUNKED("chunked"),

    MULTIPART_FORM_DATA("multipart/form-data"),

    X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),

    APPLICATION_JSON("application/json"),

    UPGRADE("Upgrade"),

    WEBSOCKET("websocket"),

    KEEPALIVE("Keep-Alive"),

    keepalive("keep-alive"),

    DEFAULT_CONTENT_TYPE("text/html; charset=utf-8"),

    CONTINUE("100-continue"),

    GZIP("gzip"),

    H2("h2"),

    H2C("h2c")
	;

    private final String name;

    HeaderValueEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
