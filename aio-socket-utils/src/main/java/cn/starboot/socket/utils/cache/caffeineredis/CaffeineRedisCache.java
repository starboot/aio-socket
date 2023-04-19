/*
 *    Copyright 2019 The aio-socket Project
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
package cn.starboot.socket.utils.cache.caffeineredis;

import cn.starboot.socket.utils.StringUtils;
import cn.starboot.socket.utils.cache.AbsCache;
import cn.starboot.socket.utils.cache.CacheChangeType;
import cn.starboot.socket.utils.cache.CacheChangedVo;
import cn.starboot.socket.utils.cache.caffeine.CaffeineCache;
import cn.starboot.socket.utils.cache.redis.RedisCache;
import cn.starboot.socket.utils.cache.redis.RedisExpireUpdateTask;
import cn.starboot.socket.utils.lock.LockUtils;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CaffeineRedisCache extends AbsCache {

	public static final String						CACHE_CHANGE_TOPIC	= "AIO_CACHE_CHANGE_TOPIC_CAFFEINE";

	private static final Logger							log					= LoggerFactory.getLogger(CaffeineRedisCache.class);

	public static Map<String, CaffeineRedisCache>	map					= new HashMap<>();

	public static RTopic							topic;

	private static boolean							inited				= false;

	CaffeineCache localCache;

	RedisCache distCache;


	public static CaffeineRedisCache getCache(String cacheName, boolean skipNull) {
		CaffeineRedisCache caffeineRedisCache = map.get(cacheName);
		if (caffeineRedisCache == null && !skipNull) {
			log.warn("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, CaffeineRedisCache.class.getSimpleName());
		}
		return caffeineRedisCache;
	}

	public static CaffeineRedisCache getCache(String cacheName) {
		return getCache(cacheName, false);
	}

	private static void init(RedissonClient redisson) {
		if (!inited) {
			synchronized (CaffeineRedisCache.class) {
				if (!inited) {
					topic = redisson.getTopic(CACHE_CHANGE_TOPIC);
					topic.addListener(CacheChangedVo.class, new MessageListener<CacheChangedVo>() {
						@Override
						public void onMessage(CharSequence channel, CacheChangedVo cacheChangedVo) {
							String clientid = cacheChangedVo.getClientId();
							if (StringUtils.isBlank(clientid)) {
								log.error("clientid is null");
								return;
							}
							if (Objects.equals(CacheChangedVo.CLIENTID, clientid)) {
								log.debug("自己发布的消息,{}", clientid);
								return;
							}

							String cacheName = cacheChangedVo.getCacheName();
							CaffeineRedisCache caffeineRedisCache = CaffeineRedisCache.getCache(cacheName);
							if (caffeineRedisCache == null) {
								log.info("不能根据cacheName[{}]找到CaffeineRedisCache对象", cacheName);
								return;
							}

							CacheChangeType type = cacheChangedVo.getType();
							if (type == CacheChangeType.PUT || type == CacheChangeType.UPDATE || type == CacheChangeType.REMOVE) {
								String key = cacheChangedVo.getKey();
								caffeineRedisCache.localCache.remove(key);
							} else if (type == CacheChangeType.CLEAR) {
								caffeineRedisCache.localCache.clear();
							}
						}
					});
					inited = true;
				}
			}
		}
	}

	public static CaffeineRedisCache register(RedissonClient redisson, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		init(redisson);

		CaffeineRedisCache caffeineRedisCache = map.get(cacheName);
		if (caffeineRedisCache == null) {
			synchronized (CaffeineRedisCache.class) {
				caffeineRedisCache = map.get(cacheName);
				if (caffeineRedisCache == null) {
					RedisCache redisCache = RedisCache.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds);

					Long timeToLiveSecondsForCaffeine = timeToLiveSeconds;
					Long timeToIdleSecondsForCaffeine = timeToIdleSeconds;

					if (timeToLiveSecondsForCaffeine != null) {
						timeToLiveSecondsForCaffeine = Math.min(timeToLiveSecondsForCaffeine, MAX_EXPIRE_IN_LOCAL);
					}
					if (timeToIdleSecondsForCaffeine != null) {
						timeToIdleSecondsForCaffeine = Math.min(timeToIdleSecondsForCaffeine, MAX_EXPIRE_IN_LOCAL);
					}
					CaffeineCache caffeineCache = CaffeineCache.register(cacheName, timeToLiveSecondsForCaffeine, timeToIdleSecondsForCaffeine);

					caffeineRedisCache = new CaffeineRedisCache(cacheName, caffeineCache, redisCache);

					caffeineRedisCache.setTimeToIdleSeconds(timeToIdleSeconds);
					caffeineRedisCache.setTimeToLiveSeconds(timeToLiveSeconds);

					map.put(cacheName, caffeineRedisCache);
				}
			}
		}
		return caffeineRedisCache;
	}

	public CaffeineRedisCache(String cacheName, CaffeineCache caffeineCache, RedisCache redisCache) {
		super(cacheName);
		this.localCache = caffeineCache;
		this.distCache = redisCache;
	}

	@Override
	public void clear() {
		localCache.clear();
		distCache.clear();

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, CacheChangeType.CLEAR);
		topic.publish(cacheChangedVo);
	}

	@Override
	public Serializable _get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}

		Serializable ret = localCache.get(key);
		if (ret == null) {
			try {
				LockUtils.runWriteOrWaitRead("_tio_cr_" + key, this, () -> {
//					@Override
//					public void read() {
//					}

//					@Override
//					public void write() {
//						Serializable ret = localCache.get(key);
						if (localCache.get(key) == null) {
							Serializable ret1 = distCache.get(key);
							if (ret1 != null) {
								localCache.put(key, ret1);
							}
						}
//					}
				});
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			ret = localCache.get(key);//(Serializable) readWriteRet.writeRet;
		} else {
			// 在本地就取到数据了，那么需要在redis那定时更新一下过期时间
			Long timeToIdleSeconds = distCache.getTimeToIdleSeconds();
			if (timeToIdleSeconds != null) {
				RedisExpireUpdateTask.add(cacheName, key, timeToIdleSeconds);
			}
		}
		return ret;
	}

	@Override
	public Iterable<String> keys() {
		return distCache.keys();
	}

	@Override
	public void put(String key, Serializable value) {
		localCache.put(key, value);
		distCache.put(key, value);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.PUT);
		topic.publish(cacheChangedVo);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		localCache.putTemporary(key, value);
		distCache.putTemporary(key, value);
	}

	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}

		localCache.remove(key);
		distCache.remove(key);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.REMOVE);
		topic.publish(cacheChangedVo);
	}

	@Override
	public long ttl(String key) {
		return distCache.ttl(key);
	}

}
