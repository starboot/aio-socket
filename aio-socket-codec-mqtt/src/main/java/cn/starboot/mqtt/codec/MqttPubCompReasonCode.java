package cn.starboot.mqtt.codec;

/**
 * Reason codes for PUBCOMP MQTT message
 *
 * @author vertx-mqtt
 * @author netty
 * @author L.cm
 * @author MDong
 */
public enum MqttPubCompReasonCode implements MqttReasonCode {

	/**
	 * PubComp ReasonCode
	 */
	SUCCESS((byte) 0x0),
	PACKET_IDENTIFIER_NOT_FOUND((byte) 0x92);

	private final byte byteValue;

	MqttPubCompReasonCode(byte byteValue) {
		this.byteValue = byteValue;
	}

	@Override
	public byte value() {
		return byteValue;
	}

	public static MqttPubCompReasonCode valueOf(byte b) {
		if (b == SUCCESS.byteValue) {
			return SUCCESS;
		} else if (b == PACKET_IDENTIFIER_NOT_FOUND.byteValue) {
			return PACKET_IDENTIFIER_NOT_FOUND;
		} else {
			throw new IllegalArgumentException("unknown PUBCOMP reason code: " + b);
		}
	}
}
