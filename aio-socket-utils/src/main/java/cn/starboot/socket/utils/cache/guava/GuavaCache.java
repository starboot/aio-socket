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
package cn.starboot.socket.utils.cache.guava;

import cn.starboot.socket.utils.StringUtils;
import cn.starboot.socket.utils.cache.AbsCache;
import cn.starboot.socket.utils.guava.GuavaUtils;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class GuavaCache extends AbsCache {

	private static final Logger log = LoggerFactory.getLogger(GuavaCache.class);

	public static Map<String, GuavaCache> map = new HashMap<>();

	public static GuavaCache getCache(String cacheName) {
		GuavaCache guavaCache = map.get(cacheName);
		if (guavaCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, GuavaCache.class.getSimpleName());
		}
		return guavaCache;
	}

	public static GuavaCache register(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		GuavaCache guavaCache = register(cacheName, timeToLiveSeconds, timeToIdleSeconds, null);
		return guavaCache;
	}

	public static GuavaCache register(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds, RemovalListener<String, Serializable> removalListener) {
		GuavaCache guavaCache = map.get(cacheName);
		if (guavaCache == null) {
			synchronized (GuavaCache.class) {
				guavaCache = map.get(cacheName);
				if (guavaCache == null) {
					Integer concurrencyLevel = 8;
					Integer initialCapacity = 10;
					Integer maximumSize = 5000000;
					boolean recordStats = false;
					LoadingCache<String, Serializable> loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, timeToLiveSeconds, timeToIdleSeconds, initialCapacity,
					        maximumSize, recordStats, removalListener);

					Integer temporaryMaximumSize = 500000;
					LoadingCache<String, Serializable> temporaryLoadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, 10L, (Long) null, initialCapacity,
					        temporaryMaximumSize, recordStats, removalListener);
					guavaCache = new GuavaCache(cacheName, loadingCache, temporaryLoadingCache);

					guavaCache.setTimeToIdleSeconds(timeToIdleSeconds);
					guavaCache.setTimeToLiveSeconds(timeToLiveSeconds);

					map.put(cacheName, guavaCache);
				}
			}
		}
		return guavaCache;
	}

	private final LoadingCache<String, Serializable> loadingCache;

	private final LoadingCache<String, Serializable> temporaryLoadingCache;

	private GuavaCache(String cacheName, LoadingCache<String, Serializable> loadingCache, LoadingCache<String, Serializable> temporaryLoadingCache) {
		super(cacheName);
		this.loadingCache = loadingCache;
		this.temporaryLoadingCache = temporaryLoadingCache;
	}

	@Override
	public void clear() {
		loadingCache.invalidateAll();
		temporaryLoadingCache.invalidateAll();
	}

	@Override
	public Serializable _get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Serializable ret = loadingCache.getIfPresent(key);
		if (ret == null) {
			ret = temporaryLoadingCache.getIfPresent(key);
		}

		return ret;
	}

	@Override
	public Collection<String> keys() {
		ConcurrentMap<String, Serializable> map = loadingCache.asMap();
		return map.keySet();
	}

	@Override
	public void put(String key, Serializable value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		loadingCache.put(key, value);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		temporaryLoadingCache.put(key, value);
	}

	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		loadingCache.invalidate(key);
		temporaryLoadingCache.invalidate(key);
	}

	public ConcurrentMap<String, Serializable> asMap() {
		return loadingCache.asMap();
	}

	public long size() {
		return loadingCache.size();
	}

	@Override
	public long ttl(String key) {
		throw new RuntimeException("不支持ttl");
	}
}
