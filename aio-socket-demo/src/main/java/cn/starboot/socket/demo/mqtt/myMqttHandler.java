package cn.starboot.socket.demo.mqtt;

import cn.starboot.mqtt.MqttHandler;
import cn.starboot.mqtt.codec.MqttMessage;
import cn.starboot.mqtt.codec.MqttMessageBuilders;
import cn.starboot.mqtt.codec.MqttQoS;
import cn.starboot.socket.Packet;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class myMqttHandler extends MqttHandler {

	@Override
	public Packet handle(ChannelContext channelContext, MqttMessage mqttMessage) {
		System.out.println(mqttMessage.toString());
		if (mqttMessage.payload() instanceof ByteBuffer) {
			ByteBuffer payload = (ByteBuffer) mqttMessage.payload();
			byte[] bytes = new byte[payload.remaining()];
			payload.get(bytes);
			System.out.println(new String(bytes, StandardCharsets.UTF_8));

			MqttMessageBuilders.PublishBuilder publish = MqttMessageBuilders.publish();

			publish.retained(false)
					.topicName("/aio-socket")
					.payload(payload)
					.messageId(-1)
					.qos(MqttQoS.AT_MOST_ONCE);
			return publish.build();

		}

		return null;
	}
}
