
package io.github.mxd888.http.common.exception;


import io.github.mxd888.http.common.enums.HttpStatus;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/3
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
