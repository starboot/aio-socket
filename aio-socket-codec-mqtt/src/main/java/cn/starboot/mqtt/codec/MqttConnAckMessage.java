package cn.starboot.mqtt.codec;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#connack">MQTTV3.1/connack</a>
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttConnAckMessage extends MqttMessage {

	public MqttConnAckMessage(MqttFixedHeader mqttFixedHeader, MqttConnAckVariableHeader variableHeader) {
		super(mqttFixedHeader, variableHeader);
	}

	@Override
	public MqttConnAckVariableHeader variableHeader() {
		return (MqttConnAckVariableHeader) super.variableHeader();
	}
}
