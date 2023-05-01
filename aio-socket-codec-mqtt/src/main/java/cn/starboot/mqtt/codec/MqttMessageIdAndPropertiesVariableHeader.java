package cn.starboot.mqtt.codec;

/**
 * Variable Header containing, Packet Id and Properties as in MQTT v5 spec.
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttMessageIdAndPropertiesVariableHeader extends MqttMessageIdVariableHeader {
	private final MqttProperties properties;

	public MqttMessageIdAndPropertiesVariableHeader(int messageId, MqttProperties properties) {
		super(messageId);
		if (messageId < 1 || messageId > 0xffff) {
			throw new IllegalArgumentException("messageId: " + messageId + " (expected: 1 ~ 65535)");
		}
		this.properties = MqttProperties.withEmptyDefaults(properties);
	}

	public MqttProperties properties() {
		return properties;
	}

	@Override
	public String toString() {
		return "MqttMessageIdAndPropertiesVariableHeader[" +
				"messageId=" + messageId() +
				", properties=" + properties +
				']';
	}

	@Override
	MqttMessageIdAndPropertiesVariableHeader withDefaultEmptyProperties() {
		return this;
	}
}
