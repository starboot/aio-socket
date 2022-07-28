
package io.github.mxd888.http.common.enums;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2021/2/4
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
