package cn.starboot.socket.core.utils.json;

import cn.starboot.socket.core.utils.json.serializer.IJsonSerializer;

import java.util.List;

/**
 * JSON工具包
 *
 * @author MDong
 */
public class JsonUtil {

	private static final IJsonSerializer jsonSerializer = JSONFactory.createSerializer();

	public static String toJSONString(Object object) {
		return jsonSerializer.toString(object);
	}

	public static byte[] toJsonBytes(Object object) {
		return jsonSerializer.toByte(object);
	}

	public static <T> T toBean(String string, Class<T> clazz) {
		return jsonSerializer.toObject(string, clazz);
	}

	public static <T> T toBean(byte[] bytes, Class<T> clazz) {
		return jsonSerializer.toObject(bytes, clazz);
	}

	public static <T> List<T> toListBean(String string, Class<T> clazz) {
		return jsonSerializer.toArray(string, clazz);
	}
}
