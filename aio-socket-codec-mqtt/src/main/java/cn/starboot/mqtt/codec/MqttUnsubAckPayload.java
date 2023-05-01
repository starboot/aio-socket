package cn.starboot.mqtt.codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Payload for MQTT unsuback message as in V5.
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttUnsubAckPayload {

	private final List<Short> unsubscribeReasonCodes;

	private static final MqttUnsubAckPayload EMPTY = new MqttUnsubAckPayload();

	public static MqttUnsubAckPayload withEmptyDefaults(MqttUnsubAckPayload payload) {
		if (payload == null) {
			return EMPTY;
		} else {
			return payload;
		}
	}

	public MqttUnsubAckPayload() {
		this.unsubscribeReasonCodes = Collections.emptyList();
	}

	public MqttUnsubAckPayload(Iterable<Short> unsubscribeReasonCodes) {
		Objects.requireNonNull(unsubscribeReasonCodes, "unsubscribeReasonCodes is null.");
		List<Short> list = new ArrayList<>();
		for (Short v : unsubscribeReasonCodes) {
			Objects.requireNonNull(v, "unsubscribeReasonCode is null.");
			list.add(v);
		}
		this.unsubscribeReasonCodes = Collections.unmodifiableList(list);
	}

	public List<Short> unsubscribeReasonCodes() {
		return unsubscribeReasonCodes;
	}

	@Override
	public String toString() {
		return "MqttUnsubAckPayload[" +
				"unsubscribeReasonCodes=" + unsubscribeReasonCodes +
				']';
	}
}
