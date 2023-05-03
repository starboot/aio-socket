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
package cn.starboot.socket.utils.cache.caffeine;

import cn.starboot.socket.utils.StringUtils;
import cn.starboot.socket.utils.cache.AbstractCache;
import cn.starboot.socket.utils.json.JsonUtil;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Caffeine
 *
 * @author t-io
 * @author MDong
 */
public class CaffeineCache extends AbstractCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaffeineCache.class);

	private static final Map<String, CaffeineCache> MAP = new HashMap<>();

	public static CaffeineCache getCache(String cacheName,
										 boolean skipNull) {
		CaffeineCache caffeineCache = MAP.get(cacheName);
		if (caffeineCache == null && !skipNull && LOGGER.isErrorEnabled()) {
			LOGGER.error("cacheName[{}]还没注册，请初始化时调用：{}.register(...)",
					cacheName,
					CaffeineCache.class.getSimpleName());
		}
		return caffeineCache;
	}

	public static CaffeineCache getCache(String cacheName) {
		return getCache(cacheName, false);
	}

	public static CaffeineCache register(String cacheName,
										 Long timeToLiveSeconds,
										 Long timeToIdleSeconds) {
		return register(cacheName, timeToLiveSeconds, timeToIdleSeconds, null);
	}

	public static CaffeineCache register(String cacheName,
										 Long timeToLiveSeconds,
										 Long timeToIdleSeconds,
										 RemovalListener<String, Serializable> removalListener) {
		CaffeineCache caffeineCache = MAP.get(cacheName);
		if (caffeineCache == null) {
			synchronized (CaffeineCache.class) {
				caffeineCache = MAP.get(cacheName);
				if (caffeineCache == null) {
					Integer initialCapacity = 10;
					Integer maximumSize = 5000000;
					boolean recordStats = false;
					LoadingCache<String, Serializable> loadingCache = CaffeineUtils
							.createLoadingCache(cacheName,
									timeToLiveSeconds,
									timeToIdleSeconds,
									initialCapacity,
									maximumSize,
									recordStats,
									removalListener);
					Integer temporaryMaximumSize = 500000;
					LoadingCache<String, Serializable> temporaryLoadingCache = CaffeineUtils
							.createLoadingCache(cacheName,
									10L,
									null,
									initialCapacity,
									temporaryMaximumSize,
									recordStats,
									removalListener);
					caffeineCache = new CaffeineCache(cacheName, loadingCache, temporaryLoadingCache);
					caffeineCache.setTimeToIdleSeconds(timeToIdleSeconds);
					caffeineCache.setTimeToLiveSeconds(timeToLiveSeconds);
					MAP.put(cacheName, caffeineCache);
				}
			}
		}
		return caffeineCache;
	}

	private final LoadingCache<String, Serializable> loadingCache;

	private final LoadingCache<String, Serializable> temporaryLoadingCache;

	private CaffeineCache(String cacheName,
						  LoadingCache<String, Serializable> loadingCache,
						  LoadingCache<String, Serializable> temporaryLoadingCache) {
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
	protected Serializable get0(String key) {
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
		return loadingCache.asMap().keySet();
	}

	@Override
	public void put(String key, Object value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		loadingCache.put(key, JsonUtil.toJSONString(value));
	}

	@Override
	public void putTemporary(String key, Object value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		temporaryLoadingCache.put(key, JsonUtil.toJSONString(value));
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
		return loadingCache.estimatedSize();
	}

	@Override
	public long ttl(String key) {
		throw new UnsupportedOperationException("unsupported ttl");
	}
}
