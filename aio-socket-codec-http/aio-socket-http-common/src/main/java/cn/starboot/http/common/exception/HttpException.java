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
