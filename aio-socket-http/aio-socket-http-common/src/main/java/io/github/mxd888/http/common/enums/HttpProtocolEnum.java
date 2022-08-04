package io.github.mxd888.http.common.enums;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum HttpProtocolEnum {
    HTTP_11("HTTP/1.1"),
    HTTP_10("HTTP/1.0"),
    ;

    private final String protocol;

    HttpProtocolEnum(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }
}
