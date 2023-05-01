package cn.starboot.mqtt.codec;

import java.util.Collections;
import java.util.List;

/**
 * Payload of the {@link MqttSubscribeMessage}
 *
 * @author netty
 */
public final class MqttSubscribePayload {

	private final List<MqttTopicSubscription> topicSubscriptions;

	public MqttSubscribePayload(List<MqttTopicSubscription> topicSubscriptions) {
		this.topicSubscriptions = Collections.unmodifiableList(topicSubscriptions);
	}

	public List<MqttTopicSubscription> topicSubscriptions() {
		return topicSubscriptions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("MqttSubscribePayload[");
		for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
			builder.append(topicSubscription).append(", ");
		}
		if (!topicSubscriptions.isEmpty()) {
			builder.setLength(builder.length() - 2);
		}
		return builder.append(']').toString();
	}
}
