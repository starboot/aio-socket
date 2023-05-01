package cn.starboot.socket.codec;

import java.util.Objects;

/**
 * 解析结果集
 *
 * @author L.cm
 */
public class DecoderResult {
	public static final DecoderResult SUCCESS = new DecoderResult();

	private final boolean success;
	private final Throwable cause;

	public DecoderResult() {
		this(true, null);
	}

	public DecoderResult(Throwable cause) {
		this(false, Objects.requireNonNull(cause, "cause is null"));
	}

	public DecoderResult(boolean success, Throwable cause) {
		this.success = success;
		this.cause = cause;
	}

	public static DecoderResult failure(Throwable cause) {
		return new DecoderResult(cause);
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFailure() {
		return !success;
	}

	public Throwable getCause() {
		return cause;
	}
}
