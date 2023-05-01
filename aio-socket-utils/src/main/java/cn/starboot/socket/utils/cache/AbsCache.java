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
package cn.starboot.socket.utils.cache;

import cn.starboot.socket.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public abstract class AbsCache implements ICache {


	private static final Logger log = LoggerFactory.getLogger(AbsCache.class);

	protected String cacheName = null;

	private Long timeToLiveSeconds;

	private Long timeToIdleSeconds;

	public AbsCache(String cacheName) {
		if (StringUtils.isBlank(cacheName)) {
			throw new RuntimeException("cacheName不允许为空");
		}
		this.setCacheName(cacheName);
	}

	public AbsCache(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		if (StringUtils.isBlank(cacheName)) {
			throw new RuntimeException("cacheName不允许为空");
		}
		this.setCacheName(cacheName);
		this.setTimeToLiveSeconds(timeToLiveSeconds);
		this.setTimeToIdleSeconds(timeToIdleSeconds);
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(Long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	public Long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public void setTimeToIdleSeconds(Long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	public Serializable get(String key) {
		Serializable obj = _get(key);
		if (obj instanceof NullClass) {
			return null;
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		return (T) get(key);
	}

	public abstract Serializable _get(String key);

}
