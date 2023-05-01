package cn.starboot.mqtt.codec;

import cn.starboot.socket.codec.DecoderException;

/**
 * A {@link MqttUnacceptableProtocolVersionException} which is thrown when
 * a CONNECT request contains unacceptable protocol version.
 */
public final class MqttUnacceptableProtocolVersionException extends DecoderException {

	private static final long serialVersionUID = 4914652213232455749L;

	/**
	 * Creates a new instance
	 */
	public MqttUnacceptableProtocolVersionException() { }

	/**
	 * Creates a new instance
	 */
	public MqttUnacceptableProtocolVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new instance
	 */
	public MqttUnacceptableProtocolVersionException(String message) {
		super(message);
	}

	/**
	 * Creates a new instance
	 */
	public MqttUnacceptableProtocolVersionException(Throwable cause) {
		super(cause);
	}

}
