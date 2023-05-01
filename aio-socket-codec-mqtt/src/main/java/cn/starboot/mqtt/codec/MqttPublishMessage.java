package cn.starboot.mqtt.codec;

import java.nio.ByteBuffer;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#publish">MQTTV3.1/publish</a>
 *
 * @author netty
 */
public class MqttPublishMessage extends MqttMessage {
	public MqttPublishMessage(
			MqttFixedHeader mqttFixedHeader,
			MqttPublishVariableHeader variableHeader,
			ByteBuffer payload) {
		super(mqttFixedHeader, variableHeader, payload);
	}

	@Override
	public MqttPublishVariableHeader variableHeader() {
		return (MqttPublishVariableHeader) super.variableHeader();
	}

	@Override
	public ByteBuffer payload() {
		return (ByteBuffer) super.payload();
	}

	public ByteBuffer getPayload() {
		return payload();
	}

}
