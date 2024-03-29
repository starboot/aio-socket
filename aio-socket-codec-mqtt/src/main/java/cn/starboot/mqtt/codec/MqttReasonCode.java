package cn.starboot.mqtt.codec;

/**
 * Common interface for MQTT messages reason codes enums
 *
 * @author vertx-mqtt
 * @author netty
 * @author L.cm
 * @author MDong
 */
public interface MqttReasonCode {

	/**
	 * byteValue
	 *
	 * @return byteValue
	 */
	byte value();

	/**
	 * isError
	 *
	 * @return boolean
	 */
	default boolean isError() {
		return (value() & 0x80) != 0;
	}

}
