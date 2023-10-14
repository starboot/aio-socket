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
