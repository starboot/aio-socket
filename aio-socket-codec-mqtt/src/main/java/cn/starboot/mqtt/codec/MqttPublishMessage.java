package cn.starboot.mqtt.codec;

import java.nio.ByteBuffer;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#publish">MQTTV3.1/publish</a>
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public class MqttPublishMessage extends MqttMessage {

	/* uid */
	private static final long serialVersionUID = 7313984585190688814L;

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
