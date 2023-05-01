package cn.starboot.mqtt.codec;

/**
 * Reason codes for UNSUBACK MQTT message
 *
 * @author vertx-mqtt
 */
public enum MqttUnsubAckReasonCode implements MqttReasonCode {

	/**
	 * UnsubAck ReasonCode
	 */
	SUCCESS((byte) 0x0),
	NO_SUBSCRIPTION_EXISTED((byte) 0x11),
	UNSPECIFIED_ERROR((byte) 0x80),
	IMPLEMENTATION_SPECIFIC_ERROR((byte) 0x83),
	NOT_AUTHORIZED((byte) 0x87),
	TOPIC_FILTER_INVALID((byte) 0x8F),
	PACKET_IDENTIFIER_IN_USE((byte) 0x91);

	private final byte byteValue;

	MqttUnsubAckReasonCode(byte byteValue) {
		this.byteValue = byteValue;
	}

	@Override
	public byte value() {
		return byteValue;
	}

}
