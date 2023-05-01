package cn.starboot.mqtt.codec;

/**
 * mqtt 常量
 *
 * @author netty
 * @author L.cm
 * @author MDong
 */
public interface MqttConstant {

	/**
	 * mqtt protocol length
	 */
	int MQTT_PROTOCOL_LENGTH = 2;

	/**
	 * 默认 最大一次读取的 byte 字节数，默认：8k
	 */
	int DEFAULT_MAX_READ_BUFFER_SIZE = 8 * 1024;

	/**
	 * Default max bytes in message，默认：10M
	 */
	int DEFAULT_MAX_BYTES_IN_MESSAGE = 10 * 1024 * 1024;

	/**
	 * min client id length
	 */
	int MIN_CLIENT_ID_LENGTH = 1;

	/**
	 * Default max client id length,In the mqtt3.1 protocol,
	 * the default maximum Client Identifier length is 23，设置成 64，减少问题
	 */
	int DEFAULT_MAX_CLIENT_ID_LENGTH = 64;
}
