package cn.starboot.mqtt.codec;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#puback">MQTTV3.1/puback</a>
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttPubAckMessage extends MqttMessage {

	/* uid */
	private static final long serialVersionUID = -1640357482029123168L;

	public MqttPubAckMessage(MqttFixedHeader mqttFixedHeader, MqttMessageIdVariableHeader variableHeader) {
		super(mqttFixedHeader, variableHeader);
	}

	@Override
	public MqttMessageIdVariableHeader variableHeader() {
		return (MqttMessageIdVariableHeader) super.variableHeader();
	}
}
