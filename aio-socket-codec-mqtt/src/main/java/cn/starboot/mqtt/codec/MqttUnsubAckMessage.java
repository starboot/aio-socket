package cn.starboot.mqtt.codec;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#unsuback">
 * MQTTV3.1/unsuback</a>
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttUnsubAckMessage extends MqttMessage {

	/* uid */
	private static final long serialVersionUID = 4523024222037459012L;

	public MqttUnsubAckMessage(MqttFixedHeader mqttFixedHeader,
							   MqttMessageIdAndPropertiesVariableHeader variableHeader,
							   MqttUnsubAckPayload payload) {
		super(mqttFixedHeader, variableHeader, MqttUnsubAckPayload.withEmptyDefaults(payload));
	}

	public MqttUnsubAckMessage(MqttFixedHeader mqttFixedHeader,
							   MqttMessageIdVariableHeader variableHeader,
							   MqttUnsubAckPayload payload) {
		this(mqttFixedHeader, fallbackVariableHeader(variableHeader), payload);
	}

	public MqttUnsubAckMessage(MqttFixedHeader mqttFixedHeader,
							   MqttMessageIdVariableHeader variableHeader) {
		this(mqttFixedHeader, variableHeader, null);
	}

	private static MqttMessageIdAndPropertiesVariableHeader fallbackVariableHeader(
			MqttMessageIdVariableHeader variableHeader) {
		if (variableHeader instanceof MqttMessageIdAndPropertiesVariableHeader) {
			return (MqttMessageIdAndPropertiesVariableHeader) variableHeader;
		}
		return new MqttMessageIdAndPropertiesVariableHeader(variableHeader.messageId(), MqttProperties.NO_PROPERTIES);
	}

	@Override
	public MqttMessageIdVariableHeader variableHeader() {
		return (MqttMessageIdVariableHeader) super.variableHeader();
	}

	public MqttMessageIdAndPropertiesVariableHeader idAndPropertiesVariableHeader() {
		return (MqttMessageIdAndPropertiesVariableHeader) super.variableHeader();
	}

	@Override
	public MqttUnsubAckPayload payload() {
		return (MqttUnsubAckPayload) super.payload();
	}
}
