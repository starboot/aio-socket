package io.github.mxd888.socket.exception;

/**
 * aio-socket编码异常处理
 */
public class AioEncoderException extends RuntimeException {

    public AioEncoderException() {
        super();
    }

    public AioEncoderException(String message) {
        super(message);
    }

    public AioEncoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public AioEncoderException(Throwable cause) {
        super(cause);
    }

    protected AioEncoderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
