/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.enums;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum HttpMethodEnum {

    OPTIONS("OPTIONS"),

    GET("GET"),

    HEAD("HEAD"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    TRACE("TRACE"),

    CONNECT("CONNECT")
	;

    private final String method;

    HttpMethodEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
