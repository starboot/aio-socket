package cn.starboot.mqtt.codec;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Payload of {@link MqttConnectMessage}
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public final class MqttConnectPayload {
	private final String clientIdentifier;
	private final MqttProperties willProperties;
	private final String willTopic;
	private final byte[] willMessage;
	private final String userName;
	private final byte[] password;

	public MqttConnectPayload(
			String clientIdentifier,
			String willTopic,
			byte[] willMessage,
			String userName,
			byte[] password) {
		this(clientIdentifier,
				MqttProperties.NO_PROPERTIES,
				willTopic,
				willMessage,
				userName,
				password);
	}

	public MqttConnectPayload(
			String clientIdentifier,
			MqttProperties willProperties,
			String willTopic,
			byte[] willMessage,
			String userName,
			byte[] password) {
		this.clientIdentifier = clientIdentifier;
		this.willProperties = MqttProperties.withEmptyDefaults(willProperties);
		this.willTopic = willTopic;
		this.willMessage = willMessage;
		this.userName = userName;
		this.password = password;
	}

	public String clientIdentifier() {
		return clientIdentifier;
	}

	public MqttProperties willProperties() {
		return willProperties;
	}

	public String willTopic() {
		return willTopic;
	}

	public byte[] willMessageInBytes() {
		return willMessage;
	}

	public String userName() {
		return userName;
	}

	public byte[] passwordInBytes() {
		return password;
	}

	public String password() {
		return password == null ? null : new String(password, StandardCharsets.UTF_8);
	}

	@Override
	public String toString() {
		return "MqttConnectPayload[" +
				"clientIdentifier=" + clientIdentifier +
				", willTopic=" + willTopic +
				", willMessage=" + Arrays.toString(willMessage) +
				", userName=" + userName +
				", password=" + Arrays.toString(password) +
				']';
	}
}
