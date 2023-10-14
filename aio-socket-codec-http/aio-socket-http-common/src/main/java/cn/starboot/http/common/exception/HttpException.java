/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.exception;

import cn.starboot.http.common.enums.HttpStatus;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpException extends RuntimeException {

    private int httpCode;

    private String desc;

    public HttpException(HttpStatus httpStatus) {
        this.httpCode = httpStatus.value();
        this.desc = httpStatus.getReasonPhrase();
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
