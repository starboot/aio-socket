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

import cn.starboot.socket.utils.cache.AbsCache;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

import java.io.Serializable;
import java.util.Collection;

public class J2Cache extends AbsCache {

	public J2Cache(String cacheName) {
		super(cacheName);
	}

	private static CacheChannel getChannel() {
		return net.oschina.j2cache.J2Cache.getChannel();
	}

	@Override
	public void clear() {
		CacheChannel cache = getChannel();
		cache.clear(cacheName);
	}

	@Override
	public Serializable _get(String key) {
		CacheChannel cache = getChannel();
		CacheObject cacheObject = cache.get(cacheName, key);
		if (cacheObject != null) {
			return (Serializable) cacheObject.getValue();
		}
		return null;
	}

	@Override
	public Collection<String> keys() {
		CacheChannel cache = getChannel();
		return cache.keys(cacheName);
	}

	@Override
	public void put(String key, Serializable value) {
		CacheChannel cache = getChannel();
		cache.set(cacheName, key, value);
	}

	@Override
	public void remove(String key) {
		CacheChannel cache = getChannel();
		cache.evict(cacheName, key);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		throw new RuntimeException("不支持防缓存穿透");
	}

	@Override
	public long ttl(String key) {
		throw new RuntimeException("不支持ttl");
	}

}
