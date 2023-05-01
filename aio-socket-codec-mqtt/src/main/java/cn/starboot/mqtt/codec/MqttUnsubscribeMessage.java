package cn.starboot.mqtt.codec;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#unsubscribe">
 * MQTTV3.1/unsubscribe</a>
 *
 * @author netty
 */
public final class MqttUnsubscribeMessage extends MqttMessage {

	public MqttUnsubscribeMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttMessageIdAndPropertiesVariableHeader variableHeader,
			MqttUnsubscribePayload payload) {
		super(mqttFixedHeader, variableHeader, payload);
	}

	public MqttUnsubscribeMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttMessageIdVariableHeader variableHeader,
			MqttUnsubscribePayload payload) {
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
	public MqttUnsubscribePayload payload() {
		return (MqttUnsubscribePayload) super.payload();
	}
}
