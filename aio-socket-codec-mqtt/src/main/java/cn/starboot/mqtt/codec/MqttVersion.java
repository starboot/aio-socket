package cn.starboot.mqtt.codec;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * matt version
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public enum MqttVersion {
	MQTT_3_1("MQIsdp", (byte) 3),
	MQTT_3_1_1("MQTT", (byte) 4),
	MQTT_5("MQTT", (byte) 5);

	private final String name;
	private final byte level;

	MqttVersion(String protocolName, byte protocolLevel) {
		name = Objects.requireNonNull(protocolName, "protocolName");
		level = protocolLevel;
	}

	public String protocolName() {
		return name;
	}

	public byte[] protocolNameBytes() {
		return name.getBytes(StandardCharsets.UTF_8);
	}

	public byte protocolLevel() {
		return level;
	}

	public static MqttVersion fromProtocolNameAndLevel(String protocolName, byte protocolLevel) {
		MqttVersion mv = null;
		switch (protocolLevel) {
			case 3:
				mv = MQTT_3_1;
				break;
			case 4:
				mv = MQTT_3_1_1;
				break;
			case 5:
				mv = MQTT_5;
				break;
			default:
				break;
		}
		if (mv == null) {
			throw new MqttUnacceptableProtocolVersionException(protocolName + " is an unknown protocol name");
		}
		if (mv.name.equals(protocolName)) {
			return mv;
		}
		throw new MqttUnacceptableProtocolVersionException(protocolName + " and " + protocolLevel + " don't match");
	}
}
