package cn.starboot.mqtt.codec;

/**
 * Utilities for MQTT message codes enums
 *
 * @author vertx-mqtt
 */
public class ReasonCodeUtils {

	protected static <C extends MqttReasonCode> void fillValuesByCode(C[] valuesByCode, C[] values) {
		for (C code : values) {
			final int unsignedByte = code.value() & 0xFF;
			valuesByCode[unsignedByte] = code;
		}
	}

	protected static <C> C codeLoopUp(C[] valuesByCode, byte b, String codeType) {
		final int unsignedByte = b & 0xFF;
		C reasonCode = null;
		try {
			reasonCode = valuesByCode[unsignedByte];
		} catch (ArrayIndexOutOfBoundsException ignored) {
			// no op
		}
		if (reasonCode == null) {
			throw new IllegalArgumentException("unknown " + codeType + " reason code: " + unsignedByte);
		}
		return reasonCode;
	}

}
