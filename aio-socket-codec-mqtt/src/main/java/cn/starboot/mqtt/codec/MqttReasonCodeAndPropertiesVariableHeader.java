package cn.starboot.mqtt.codec;

/**
 * Variable Header for AUTH and DISCONNECT messages represented by {@link MqttMessage}
 */
public final class MqttReasonCodeAndPropertiesVariableHeader {

	private final byte reasonCode;
	private final MqttProperties properties;

	public static final byte REASON_CODE_OK = 0;

	public MqttReasonCodeAndPropertiesVariableHeader(byte reasonCode,
													 MqttProperties properties) {
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
		return "MqttReasonCodeAndPropertiesVariableHeader[" +
				"reasonCode=" + reasonCode +
				", properties=" + properties +
				']';
	}
}
