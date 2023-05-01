package cn.starboot.mqtt.codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Payload of the {@link MqttSubAckMessage}
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public class MqttSubAckPayload {
	private final List<Integer> reasonCodes;

	public MqttSubAckPayload(int... reasonCodes) {
		Objects.requireNonNull(reasonCodes, "reasonCodes is null.");
		List<Integer> list = new ArrayList<>(reasonCodes.length);
		for (int v : reasonCodes) {
			list.add(v);
		}
		this.reasonCodes = Collections.unmodifiableList(list);
	}

	public MqttSubAckPayload(Iterable<Integer> reasonCodes) {
		Objects.requireNonNull(reasonCodes, "reasonCodes is null.");
		List<Integer> list = new ArrayList<>();
		for (Integer v : reasonCodes) {
			if (v == null) {
				break;
			}
			list.add(v);
		}
		this.reasonCodes = Collections.unmodifiableList(list);
	}

	public List<Integer> grantedQoSLevels() {
		List<Integer> qosLevels = new ArrayList<>(reasonCodes.size());
		for (int code : reasonCodes) {
			if (code > MqttQoS.EXACTLY_ONCE.value()) {
				qosLevels.add(MqttQoS.FAILURE.value());
			} else {
				qosLevels.add(code);
			}
		}
		return qosLevels;
	}

	public List<Integer> reasonCodes() {
		return reasonCodes;
	}

	@Override
	public String toString() {
		return "MqttSubAckPayload[" +
				"reasonCodes=" + reasonCodes +
				']';
	}
}
