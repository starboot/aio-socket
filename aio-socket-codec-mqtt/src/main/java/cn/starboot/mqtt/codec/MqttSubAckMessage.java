package cn.starboot.mqtt.codec;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#suback">MQTTV3.1/suback</a>
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttSubAckMessage extends MqttMessage {

	public MqttSubAckMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttMessageIdAndPropertiesVariableHeader variableHeader,
			MqttSubAckPayload payload) {
		super(mqttFixedHeader, variableHeader, payload);
	}

	public MqttSubAckMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttMessageIdVariableHeader variableHeader,
			MqttSubAckPayload payload) {
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
	public MqttSubAckPayload payload() {
		return (MqttSubAckPayload) super.payload();
	}
}
