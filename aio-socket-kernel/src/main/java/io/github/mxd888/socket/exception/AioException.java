package io.github.mxd888.socket.exception;

/**
 * aio-socket异常处理类
 */
public class AioException extends Exception{

    public AioException() {
        super();
    }

    public AioException(String message) {
        super(message);
    }

    public AioException(String message, Throwable cause) {
        super(message, cause);
    }

    public AioException(Throwable cause) {
        super(cause);
    }

    protected AioException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
