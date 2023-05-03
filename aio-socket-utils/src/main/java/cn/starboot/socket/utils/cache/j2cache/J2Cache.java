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
package cn.starboot.socket.utils.cache.j2cache;

import cn.starboot.socket.utils.cache.AbstractCache;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * J2Cache
 *
 * @author t-io
 * @author MDong
 */
public class J2Cache extends AbstractCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(J2Cache.class);

	private static final Map<String, J2Cache> MAP = new HashMap<>();

	public static J2Cache getCache(String cacheName) {
		J2Cache j2Cache = MAP.get(cacheName);
		if (Objects.isNull(j2Cache) && LOGGER.isErrorEnabled()) {
			LOGGER.error("cacheName[{}]还没注册，请初始化时调用：{}.register(...)",
					cacheName,
					J2Cache.class.getSimpleName());
		}
		return j2Cache;
	}

	public static J2Cache register(String cacheName) {
		J2Cache j2Cache = MAP.get(cacheName);
		if (Objects.isNull(j2Cache)) {
			synchronized (J2Cache.class) {
				j2Cache = MAP.get(cacheName);
				if (Objects.isNull(j2Cache)) {
					j2Cache = new J2Cache(cacheName);
				}
				MAP.put(cacheName, j2Cache);
			}
		}
		return j2Cache;
	}

	private final CacheChannel cacheChannel;

	private J2Cache(String cacheName) {
		super(cacheName);
		this.cacheChannel = net.oschina.j2cache.J2Cache.getChannel();
	}

	private CacheChannel getChannel() {
		return this.cacheChannel;
	}

	@Override
	public void clear() {
		getChannel().clear(cacheName);
	}

	@Override
	protected Serializable get0(String key) {
		CacheObject cacheObject = getChannel().get(cacheName, key);
		if (cacheObject != null) {
			return cacheObject.getValue().toString();
		}
		return null;
	}

	@Override
	public Collection<String> keys() {
		return getChannel().keys(cacheName);
	}

	@Override
	public void put(String key, Object value) {
		getChannel().set(cacheName, key, value);
	}

	@Override
	public void remove(String key) {
		getChannel().evict(cacheName, key);
	}

	@Override
	public void putTemporary(String key, Object value) {
		throw new UnsupportedOperationException("不支持防缓存穿透");
	}

	@Override
	public long ttl(String key) {
		throw new UnsupportedOperationException("不支持ttl");
	}

}
