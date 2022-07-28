
package io.github.mxd888.http.common.enums;

/**
 * Http支持的Method
 *
 * @author 三刀
 * @version V1.0 , 2018/2/6
 */
public enum HttpMethodEnum {
    OPTIONS("OPTIONS"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    CONNECT("CONNECT");

    private final String method;

    HttpMethodEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
