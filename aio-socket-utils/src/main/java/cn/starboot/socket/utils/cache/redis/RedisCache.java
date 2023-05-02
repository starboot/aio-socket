package cn.starboot.socket.utils.cache.redis;

import cn.starboot.socket.utils.StringUtils;
import cn.starboot.socket.utils.cache.AbsCache;
import cn.starboot.socket.utils.SystemTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.FlushMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCache extends AbsCache {

	private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

	private static final Map<String, RedisCache> map = new HashMap<>();

	public static final String SPLIT_FOR_CACHE_NAME = ":";

	public static String cacheKey(String cacheName, String key) {
		return keyPrefix(cacheName) + key;
	}

	public static RedisCache getCache(String cacheName) {
		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, RedisCache.class.getSimpleName());
		}
		return redisCache;
	}

	public static String keyPrefix(String cacheName) {
		return cacheName + SPLIT_FOR_CACHE_NAME;
	}

	public static RedisCache register(Jedis jedis, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		RedisExpireUpdateTask.start();

		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			synchronized (RedisCache.class) {
				redisCache = map.get(cacheName);
				if (redisCache == null) {
					redisCache = new RedisCache(jedis, cacheName, timeToLiveSeconds, timeToIdleSeconds);

					redisCache.setTimeToIdleSeconds(timeToIdleSeconds);
					redisCache.setTimeToLiveSeconds(timeToLiveSeconds);
					map.put(cacheName, redisCache);
				}
			}
		}
		return redisCache;
	}

	private final Jedis jedis;

	private final Long timeToLiveSeconds;

	private final Long timeToIdleSeconds;

	private final Long timeout;

	private RedisCache(Jedis jedis, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		super(cacheName);
		this.jedis = jedis;
		this.timeToLiveSeconds = timeToLiveSeconds;
		this.timeToIdleSeconds = timeToIdleSeconds;
		this.timeout = this.timeToLiveSeconds == null ? this.timeToIdleSeconds : this.timeToLiveSeconds;

	}

	@Override
	public void clear() {
		jedis.flushDB(FlushMode.valueOf(keyPrefix(cacheName) + "*"));
	}

	@Override
	public Serializable _get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}

		String s = jedis.get(key);

		if (timeToIdleSeconds != null) {
//			if (ret != null) {
				// bucket.expire(timeout, TimeUnit.SECONDS);
				RedisExpireUpdateTask.add(cacheName, key, timeout);
//			}
		}
		return s;
	}

	public String getBucket(String key) {
		key = cacheKey(cacheName, key);
//		RBucket<Serializable> bucket = redisson.getBucket(key);
		return jedis.get(key);
	}

	public Jedis getJedis() {
		return jedis;
	}

	public Long getTimeout() {
		return timeout;
	}

	public Long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public Long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	@Override
	public Iterable<String> keys() {
		return jedis.keys(keyPrefix(cacheName) + "*");
	}

	@Override
	public void put(String key, Serializable value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
//		RBucket<Serializable> bucket = getBucket(key);

		long _timeout = timeout;
		if (timeToLiveSeconds != null && timeToLiveSeconds > 0) { //是按timeToLiveSeconds来的
			long ttl = ttl(key);
			if (ttl > 0) {
				_timeout = ttl / 1000;
			}
		}

//		bucket.set(value, _timeout, TimeUnit.SECONDS);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
//		RBucket<Serializable> bucket = getBucket(key);
//		bucket.set(value, 10, TimeUnit.SECONDS);
	}

	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
//		RBucket<Serializable> bucket = getBucket(key);
//		bucket.delete();
	}

	@Override
	public long ttl(String key) {
//		RBucket<Serializable> bucket = getBucket(key);
//		if (bucket == null) {
//			return -2L;
//		}
//		long remainTimeToLive = bucket.remainTimeToLive();
		return 5L;
	}

}
