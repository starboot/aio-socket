package cn.starboot.socket.utils.convert;

/**
 * 从F类型转到T类型
 * @param <T> T
 */
public interface Converter<T> {

	T convert(Object value);
}
