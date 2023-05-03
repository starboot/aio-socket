package cn.starboot.mqtt.codec;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#connect">MQTTV3.1/connect</a>
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttConnectMessage extends MqttMessage {

	/* uid */
	private static final long serialVersionUID = -1232964487469895340L;

	public MqttConnectMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttConnectVariableHeader variableHeader,
			MqttConnectPayload payload) {
		super(mqttFixedHeader, variableHeader, payload);
	}

	@Override
	public MqttConnectVariableHeader variableHeader() {
		return (MqttConnectVariableHeader) super.variableHeader();
	}

	@Override
	public MqttConnectPayload payload() {
		return (MqttConnectPayload) super.payload();
	}
}
