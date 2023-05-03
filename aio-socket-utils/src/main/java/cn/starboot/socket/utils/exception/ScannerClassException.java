package cn.starboot.socket.utils.exception;

public class ScannerClassException extends RuntimeException {

	/* uid */
	private static final long serialVersionUID = 5150394334878098214L;

	public ScannerClassException() {
		super();
	}

	public ScannerClassException(String message) {
		super(message);
	}

	public ScannerClassException(String message,
								 Throwable cause) {
		super(message, cause);
	}

	public ScannerClassException(Throwable cause) {
		super(cause);
	}

	protected ScannerClassException(String message,
									Throwable cause,
									boolean enableSuppression,
									boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
