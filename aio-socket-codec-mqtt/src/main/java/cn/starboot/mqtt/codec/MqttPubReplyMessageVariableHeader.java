package cn.starboot.mqtt.codec;

/**
 * Variable Header containing Packet Id, reason code and Properties as in MQTT v5 spec.
 *
 * @author netty
 */
public final class MqttPubReplyMessageVariableHeader extends MqttMessageIdVariableHeader {
	private final byte reasonCode;
	private final MqttProperties properties;

	public static final byte REASON_CODE_OK = 0;

	public MqttPubReplyMessageVariableHeader(int messageId, byte reasonCode, MqttProperties properties) {
		super(messageId);
		if (messageId < 1 || messageId > 0xffff) {
			throw new IllegalArgumentException("messageId: " + messageId + " (expected: 1 ~ 65535)");
		}
		this.reasonCode = reasonCode;
		this.properties = MqttProperties.withEmptyDefaults(properties);
	}

	public byte reasonCode() {
		return reasonCode;
	}

	public MqttProperties properties() {
		return properties;
	}

	@Override
	public String toString() {
		return "MqttPubReplyMessageVariableHeader[" +
				"messageId=" + messageId() +
				", reasonCode=" + reasonCode +
				", properties=" + properties +
				']';
	}
}
