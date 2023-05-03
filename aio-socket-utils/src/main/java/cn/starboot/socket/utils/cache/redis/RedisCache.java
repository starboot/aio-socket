package cn.starboot.socket.utils.cache.redis;

import cn.starboot.socket.utils.StringUtils;
import cn.starboot.socket.utils.cache.AbsCache;

import cn.starboot.socket.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.FlushMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * RedisCache
 *
 * @author t-io
 * @author MDong
 */
public class RedisCache extends AbsCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

	private static final Map<String, RedisCache> map = new HashMap<>();

	public static final String SPLIT_FOR_CACHE_NAME = ":";

	public static String cacheKey(String cacheName, String key) {
		return keyPrefix(cacheName) + key;
	}

	public static RedisCache getCache(String cacheName) {
		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			LOGGER.error("cacheName[{}]还没注册，请初始化时调用：{}.register(...)", cacheName, RedisCache.class.getSimpleName());
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
	protected Serializable get0(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String s = getBucket(key);
		if (timeToIdleSeconds != null) {
			if (s != null && s.length() > 0) {
				RedisExpireUpdateTask.add(cacheName, key, timeout);
			}
		}
		return s;
	}

	private String getBucket(String key) {
		return jedis.get(cacheKey(cacheName, key));
	}

	public Jedis getRedis() {
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

	public void updateTimeout(String key, long timeout) {
		if (timeout > 0) {
			jedis.expire(key, timeout);
		} else {
			Serializable serializable = get(key);
			if (Objects.nonNull(serializable)) {
				put(key, serializable);
			}
		}
	}

	@Override
	public Iterable<String> keys() {
		return jedis.keys(keyPrefix(cacheName) + "*");
	}

	@Override
	public void put(String key, Object value) {
		if (StringUtils.isBlank(key) && Objects.isNull(value)) {
			return;
		}
		key = cacheKey(cacheName, key);
		if (value instanceof String) {
			put0(key, (String) value);
		} else
			put0(key, JsonUtil.toJSONString(value));

	}

	private void put0(String key, String value) {
		jedis.set(key, value);
		updateTimeout(key, timeout);
	}

	@Override
	public void putTemporary(String key, Object value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		key = cacheKey(cacheName, key);
		jedis.set(key, JsonUtil.toJSONString(value));
		updateTimeout(key, 10L);
	}

	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		key = cacheKey(cacheName, key);
		jedis.move(key, 1);
	}

	@Override
	public long ttl(String key) {
		key = cacheKey(cacheName, key);
		return jedis.exists(key) ? jedis.ttl(key) : -2L;
	}

}
