package cn.starboot.mqtt.codec;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#subscribe">
 * MQTTV3.1/subscribe</a>
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttSubscribeMessage extends MqttMessage {

	/* uid */
	private static final long serialVersionUID = -78467029556792979L;

	public MqttSubscribeMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttMessageIdAndPropertiesVariableHeader variableHeader,
			MqttSubscribePayload payload) {
		super(mqttFixedHeader, variableHeader, payload);
	}

	public MqttSubscribeMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttMessageIdVariableHeader variableHeader,
			MqttSubscribePayload payload) {
		this(mqttFixedHeader, variableHeader.withDefaultEmptyProperties(), payload);
	}

	@Override
	public MqttMessageIdVariableHeader variableHeader() {
		return (MqttMessageIdVariableHeader) super.variableHeader();
	}

	public MqttMessageIdAndPropertiesVariableHeader idAndPropertiesVariableHeader() {
		return (MqttMessageIdAndPropertiesVariableHeader) super.variableHeader();
	}

	@Override
	public MqttSubscribePayload payload() {
		return (MqttSubscribePayload) super.payload();
	}
}
