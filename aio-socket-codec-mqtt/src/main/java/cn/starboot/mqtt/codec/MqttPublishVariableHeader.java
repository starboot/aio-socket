package cn.starboot.mqtt.codec;

/**
 * Variable Header of the {@link MqttPublishMessage}
 *
 * @author netty
 */
public final class MqttPublishVariableHeader {
	private final String topicName;
	private final int packetId;
	private final MqttProperties properties;

	public MqttPublishVariableHeader(String topicName, int packetId) {
		this(topicName, packetId, MqttProperties.NO_PROPERTIES);
	}

	public MqttPublishVariableHeader(String topicName, int packetId, MqttProperties properties) {
		this.topicName = topicName;
		this.packetId = packetId;
		this.properties = MqttProperties.withEmptyDefaults(properties);
	}

	public String topicName() {
		return topicName;
	}

	public int packetId() {
		return packetId;
	}

	public MqttProperties properties() {
		return properties;
	}

	@Override
	public String toString() {
		return "MqttPublishVariableHeader[" +
				"topicName=" + topicName +
				", packetId=" + packetId +
				']';
	}
}
