package cn.starboot.mqtt.codec;

import cn.starboot.socket.codec.DecoderResult;

import java.nio.ByteBuffer;

/**
 * Utility class with factory methods to create different types of MQTT messages.
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttMessageFactory {

	public static MqttMessage newMessage(MqttFixedHeader mqttFixedHeader, Object variableHeader, Object payload) {
		switch (mqttFixedHeader.messageType()) {
			case CONNECT:
				return new MqttConnectMessage(
						mqttFixedHeader,
						(MqttConnectVariableHeader) variableHeader,
						(MqttConnectPayload) payload);
			case CONNACK:
				return new MqttConnAckMessage(mqttFixedHeader, (MqttConnAckVariableHeader) variableHeader);
			case SUBSCRIBE:
				return new MqttSubscribeMessage(
						mqttFixedHeader,
						(MqttMessageIdVariableHeader) variableHeader,
						(MqttSubscribePayload) payload);
			case SUBACK:
				return new MqttSubAckMessage(
						mqttFixedHeader,
						(MqttMessageIdVariableHeader) variableHeader,
						(MqttSubAckPayload) payload);
			case UNSUBACK:
				return new MqttUnsubAckMessage(
						mqttFixedHeader,
						(MqttMessageIdVariableHeader) variableHeader,
						(MqttUnsubAckPayload) payload);
			case UNSUBSCRIBE:
				return new MqttUnsubscribeMessage(
						mqttFixedHeader,
						(MqttMessageIdVariableHeader) variableHeader,
						(MqttUnsubscribePayload) payload);
			case PUBLISH:
				return new MqttPublishMessage(
						mqttFixedHeader,
						(MqttPublishVariableHeader) variableHeader,
						(ByteBuffer) payload);
			case PUBACK:
				//Having MqttPubReplyMessageVariableHeader or MqttMessageIdVariableHeader
				return new MqttPubAckMessage(mqttFixedHeader, (MqttMessageIdVariableHeader) variableHeader);
			case PUBREC:
			case PUBREL:
			case PUBCOMP:
				//Having MqttPubReplyMessageVariableHeader or MqttMessageIdVariableHeader
				return new MqttMessage(mqttFixedHeader, variableHeader);
			case PINGREQ:
			case PINGRESP:
				return new MqttMessage(mqttFixedHeader);
			case DISCONNECT:
			case AUTH:
				//Having MqttReasonCodeAndPropertiesVariableHeader
				return new MqttMessage(mqttFixedHeader, variableHeader);
			default:
				throw new IllegalArgumentException("unknown message type: " + mqttFixedHeader.messageType());
		}
	}

	public static MqttMessage newInvalidMessage(Throwable cause) {
		return new MqttMessage(null, null, null, DecoderResult.failure(cause));
	}

	public static MqttMessage newInvalidMessage(MqttFixedHeader mqttFixedHeader, Object variableHeader,
												Throwable cause) {
		return new MqttMessage(mqttFixedHeader, variableHeader, null, DecoderResult.failure(cause));
	}

	private MqttMessageFactory() {
	}
}
