package cn.starboot.socket.core.exception;

public class AioParameterException extends Exception {

	private static final long serialVersionUID = -3890178296625397429L;

	public AioParameterException() {
		super();
	}

	public AioParameterException(String message) {
		super(message);
	}

	public AioParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public AioParameterException(Throwable cause) {
		super(cause);
	}

	protected AioParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
