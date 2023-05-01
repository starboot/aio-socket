package cn.starboot.mqtt.codec;

/**
 * Variable header of {@link MqttConnectMessage}
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttConnAckVariableHeader {
	private final MqttConnectReasonCode connectReturnCode;

	private final boolean sessionPresent;

	private final MqttProperties properties;

	public MqttConnAckVariableHeader(MqttConnectReasonCode connectReturnCode, boolean sessionPresent) {
		this(connectReturnCode, sessionPresent, MqttProperties.NO_PROPERTIES);
	}

	public MqttConnAckVariableHeader(MqttConnectReasonCode connectReturnCode, boolean sessionPresent,
									 MqttProperties properties) {
		this.connectReturnCode = connectReturnCode;
		this.sessionPresent = sessionPresent;
		this.properties = MqttProperties.withEmptyDefaults(properties);
	}

	public MqttConnectReasonCode connectReturnCode() {
		return connectReturnCode;
	}

	public boolean isSessionPresent() {
		return sessionPresent;
	}

	public MqttProperties properties() {
		return properties;
	}

	@Override
	public String toString() {
		return "MqttConnAckVariableHeader[" +
				"connectReturnCode=" + connectReturnCode +
				", sessionPresent=" + sessionPresent +
				']';
	}
}
