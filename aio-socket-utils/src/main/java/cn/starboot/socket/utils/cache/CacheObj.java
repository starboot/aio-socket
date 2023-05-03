package cn.starboot.socket.utils.cache;

import cn.starboot.socket.utils.json.JsonUtil;

import java.io.Serializable;

/**
 * 所有使用缓存的都要继承此对象
 *
 * @author MDong
 */
public class CacheObj implements Serializable {

	/* uid */
	private static final long serialVersionUID = 7405927559870599104L;

	/**
	 * 不要重写此方法
	 * 不需要担心转化性能，fast json2性能无敌
	 *
	 * @return 所有缓存对象均使用JSON格式存储
	 */
	@Override
	public String toString() {
		return JsonUtil.toJSONString(this);
	}
}
