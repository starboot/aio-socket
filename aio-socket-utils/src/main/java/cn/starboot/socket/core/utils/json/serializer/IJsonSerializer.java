package cn.starboot.socket.core.utils.json.serializer;

import java.util.List;

/**
 * Created by DELL(mxd) on 2021/12/24 14:03
 */
public interface IJsonSerializer {

    /*
     * 序列化某个对象
     * */
    <T> String toString(T t);

    /*
     * 反序列化
     * */
    <T> T toObject(String json, Class<T> clazz);

	/**
	 * 序列化成数组
	 *
	 * @param json 待序列化数据
	 * @param clazz 对象
	 * @param <T> 类型
	 * @return List数组对线
	 */
	<T> List<T> toArray(String json, Class<T> clazz);

	/**
	 * 反序列化
	 *
	 * @param bytes 代加工数据
	 * @param clazz 对象
	 * @param <T> 类型
	 * @return 指定类型对象
	 */
    <T> T toObject(byte[] bytes, Class<T> clazz);

	/**
	 * 获取byte数组
	 *
	 * @param t 待序列化对象
	 * @param <T> 类型
	 * @return byte数组
	 */
    <T> byte[] toByte(T t);

}
