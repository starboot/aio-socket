package cn.starboot.mqtt.codec;

import java.util.Collections;
import java.util.List;

/**
 * Payload of the {@link MqttUnsubscribeMessage}
 *
 * @author netty
 */
public final class MqttUnsubscribePayload {
	private final List<String> topics;

	public MqttUnsubscribePayload(List<String> topics) {
		this.topics = Collections.unmodifiableList(topics);
	}

	public List<String> topics() {
		return topics;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("MqttUnsubscribePayload[");
		for (String topic : topics) {
			builder.append("topicName = ").append(topic).append(", ");
		}
		if (!topics.isEmpty()) {
			builder.setLength(builder.length() - 2);
		}
		return builder.append(']').toString();
	}

}
