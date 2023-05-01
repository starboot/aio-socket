package cn.starboot.mqtt.codec;

/**
 * Reason codes for PUBREL MQTT message
 *
 * @author vertx-mqtt
 * @author netty
 * @author L.cm
 * @author MDong
 */
public enum MqttPubRelReasonCode implements MqttReasonCode {

	/**
	 * PubRel ReasonCode
	 */
	SUCCESS((byte) 0x0),
	PACKET_IDENTIFIER_NOT_FOUND((byte) 0x92);

	private final byte byteValue;

	MqttPubRelReasonCode(byte byteValue) {
		this.byteValue = byteValue;
	}

	@Override
	public byte value() {
		return byteValue;
	}

	public static MqttPubRelReasonCode valueOf(byte b) {
		if (b == SUCCESS.byteValue) {
			return SUCCESS;
		} else if (b == PACKET_IDENTIFIER_NOT_FOUND.byteValue) {
			return PACKET_IDENTIFIER_NOT_FOUND;
		} else {
			throw new IllegalArgumentException("unknown PUBREL reason code: " + b);
		}
	}
}
