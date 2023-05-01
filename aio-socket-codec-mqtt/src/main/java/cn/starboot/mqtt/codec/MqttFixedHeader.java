package cn.starboot.mqtt.codec;

import java.util.Objects;

/**
 * See <a href="https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#fixed-header">
 * MQTTV3.1/fixed-header</a>
 *
 * @author netty、L.cm
 */
public final class MqttFixedHeader {

	private final MqttMessageType messageType;
	private final boolean isDup;
	private MqttQoS qosLevel;
	private final boolean isRetain;
	private final int headLength;
	private final int remainingLength;

	public MqttFixedHeader(
			MqttMessageType messageType,
			boolean isDup,
			MqttQoS qosLevel,
			boolean isRetain,
			int remainingLength) {
		this(messageType, isDup, qosLevel, isRetain, 0, remainingLength);
	}

	public MqttFixedHeader(
			MqttMessageType messageType,
			boolean isDup,
			MqttQoS qosLevel,
			boolean isRetain,
			int headLength,
			int remainingLength) {
		this.messageType = Objects.requireNonNull(messageType, "messageType is null.");
		this.isDup = isDup;
		this.qosLevel = Objects.requireNonNull(qosLevel, "qosLevel is null.");
		this.isRetain = isRetain;
		this.headLength = headLength;
		this.remainingLength = remainingLength;
	}

	public MqttMessageType messageType() {
		return messageType;
	}

	public boolean isDup() {
		return isDup;
	}

	public MqttQoS qosLevel() {
		return qosLevel;
	}

	/**
	 * 做 qos 降级，mqtt 规定 qos > 0，messageId 必须大于 0，为了兼容，固做降级处理
	 */
	void downgradeQos() {
		this.qosLevel = MqttQoS.AT_MOST_ONCE;
	}

	public boolean isRetain() {
		return isRetain;
	}

	public int headLength() {
		return headLength;
	}

	public int remainingLength() {
		return remainingLength;
	}

	public int getMessageLength() {
		return headLength + remainingLength;
	}

	@Override
	public String toString() {
		return "MqttFixedHeader[" +
				"messageType=" + messageType +
				", isDup=" + isDup +
				", qosLevel=" + qosLevel +
				", isRetain=" + isRetain +
				", remainingLength=" + remainingLength +
				']';
	}
}
