/*
 *    Copyright 2020 The t-io Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.core.utils.cache.redis;

import cn.starboot.socket.core.utils.StringUtils;
import cn.starboot.socket.core.utils.cache.AbstractCache;

import cn.starboot.socket.core.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * RedisCache
 *
 * 对缓存的封装，包括Redis、Caffeine和j2cache，
 * 此部分源代码出自talent-tan的开源项目t-io。
 *
 * @author t-io: https://gitee.com/tywo45/t-io.git
 * @author MDong
 */
public class RedisCache extends AbstractCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

	private static final Map<String, RedisCache> MAP = new HashMap<>();

	public static final String SPLIT_FOR_CACHE_NAME = ":";

	private static RedisExpireUpdateService redisExpireUpdateTask;

	public static RedisCache getCache(String cacheName) {
		RedisCache redisCache = MAP.get(cacheName);
		if (redisCache == null && LOGGER.isErrorEnabled()) {
			LOGGER.error("cacheName[{}]还没注册，请初始化时调用：{}.register(...)",
					cacheName,
					RedisCache.class.getSimpleName());
		}
		return redisCache;
	}


	public static RedisCache register(Jedis jedis,
									  String cacheName,
									  Long timeToLiveSeconds,
									  Long timeToIdleSeconds) {
		return register(jedis, cacheName, timeToLiveSeconds, timeToIdleSeconds, null);
	}

	public static RedisCache register(Jedis jedis,
									  String cacheName,
									  Long timeToLiveSeconds,
									  Long timeToIdleSeconds,
									  Integer seconds) {
		if (Objects.nonNull(seconds)) {
			redisExpireUpdateTask = RedisExpireUpdateService.getInstance(seconds);
		}
		RedisCache redisCache = MAP.get(cacheName);
		if (redisCache == null) {
			synchronized (RedisCache.class) {
				redisCache = MAP.get(cacheName);
				if (redisCache == null) {
					redisCache = new RedisCache(jedis, cacheName, timeToLiveSeconds, timeToIdleSeconds);
					redisCache.setTimeToIdleSeconds(timeToIdleSeconds);
					redisCache.setTimeToLiveSeconds(timeToLiveSeconds);
					MAP.put(cacheName, redisCache);
				}
			}
		}
		return redisCache;
	}

	public static String cacheKey(String cacheName, String key) {
		return keyPrefix(cacheName) + key;
	}

	public static String keyPrefix(String cacheName) {
		return cacheName + SPLIT_FOR_CACHE_NAME;
	}

	private final Jedis jedis;

	private final Integer timeout;

	private RedisCache(Jedis jedis, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		super(cacheName, timeToLiveSeconds, timeToIdleSeconds);
		this.jedis = jedis;
		this.timeout = Math.toIntExact(timeToLiveSeconds == null ? timeToIdleSeconds : timeToLiveSeconds);

	}

	@Override
	public void clear() {
		jedis.flushDB();
	}

	@Override
	protected Serializable get0(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String s = getBucket(key);
		if (getTimeToIdleSeconds() != null) {
			if (s != null && s.length() > 0 && redisExpireUpdateTask.getStatus()) {
				redisExpireUpdateTask.add(cacheName, key, s);
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

	public Integer getTimeout() {
		return timeout;
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
		jedis.expire(key, timeout);
	}

	@Override
	public void putTemporary(String key, Object value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		key = cacheKey(cacheName, key);
		jedis.set(key, JsonUtil.toJSONString(value));
		jedis.expire(key, 10);
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
