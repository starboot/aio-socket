package cn.starboot.mqtt.codec;

/**
 * Reason codes for PUBREC MQTT message
 *
 * @author vertx-mqtt
 * @author netty
 * @author L.cm
 * @author MDong
 */
public enum MqttPubRecReasonCode implements MqttReasonCode {

	/**
	 * PubRec ReasonCode
	 */
	SUCCESS((byte) 0x0),
	NO_MATCHING_SUBSCRIBERS((byte) 0x10),
	UNSPECIFIED_ERROR((byte) 0x80),
	IMPLEMENTATION_SPECIFIC_ERROR((byte) 0x83),
	NOT_AUTHORIZED((byte) 0x87),
	TOPIC_NAME_INVALID((byte) 0x90),
	PACKET_IDENTIFIER_IN_USE((byte) 0x91),
	QUOTA_EXCEEDED((byte) 0x97),
	PAYLOAD_FORMAT_INVALID((byte) 0x99);

	private static final MqttPubRecReasonCode[] VALUES = new MqttPubRecReasonCode[0x9A];

	static {
		ReasonCodeUtils.fillValuesByCode(VALUES, values());
	}

	private final byte byteValue;

	MqttPubRecReasonCode(byte byteValue) {
		this.byteValue = byteValue;
	}

	@Override
	public byte value() {
		return byteValue;
	}

	@Override
	public boolean isError() {
		return Byte.toUnsignedInt(byteValue) >= Byte.toUnsignedInt(UNSPECIFIED_ERROR.byteValue);
	}

	public static MqttPubRecReasonCode valueOf(byte b) {
		return ReasonCodeUtils.codeLoopUp(VALUES, b, "PUBREC");
	}

}
