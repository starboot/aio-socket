package io.github.mxd888.socket.utils.exception;

public class ScannerClassException extends RuntimeException{
    public ScannerClassException() {
        super();
    }

    public ScannerClassException(String message) {
        super(message);
    }

    public ScannerClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScannerClassException(Throwable cause) {
        super(cause);
    }

    protected ScannerClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
