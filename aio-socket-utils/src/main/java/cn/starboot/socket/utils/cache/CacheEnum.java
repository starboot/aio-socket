package cn.starboot.socket.utils.cache;

/**
 * Cache 类型
 *
 * @author MDong
 */
public enum CacheEnum {

	/**
	 * 获取Redis客户端
	 */
	REDIS,

	/**
	 * Caffeine
	 */
	CAFFEINE,

	/**
	 * J2Cache
	 */
	J2CACHE
}
