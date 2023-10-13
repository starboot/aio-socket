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
package cn.starboot.socket.utils.cache;

import cn.starboot.socket.utils.StringUtils;
import cn.starboot.socket.utils.json.JsonUtil;

import java.io.Serializable;

/**
 * 对缓存的封装，包括Redis、Caffeine和j2cache，
 * 此部分源代码出自talent-tan的开源项目t-io。
 *
 * @author t-io: https://gitee.com/tywo45/t-io.git
 * @author MDong
 */
public abstract class AbstractCache implements ICache {

	protected String cacheName = null;

	private Long timeToLiveSeconds;

	private Long timeToIdleSeconds;

	public AbstractCache(String cacheName) {
		if (StringUtils.isBlank(cacheName)) {
			throw new RuntimeException("cacheName not null");
		}
		this.setCacheName(cacheName);
	}

	public AbstractCache(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		if (StringUtils.isBlank(cacheName)) {
			throw new RuntimeException("cacheName not null");
		}
		this.setCacheName(cacheName);
		this.setTimeToLiveSeconds(timeToLiveSeconds);
		this.setTimeToIdleSeconds(timeToIdleSeconds);
	}

	@Override
	public String getCacheName() {
		return cacheName;
	}

	void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	@Override
	public Long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	protected void setTimeToLiveSeconds(Long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	@Override
	public Long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	protected void setTimeToIdleSeconds(Long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	@Override
	public Serializable get(String key) {
		Serializable obj = get0(key);
		if (obj instanceof NullClass) {
			return null;
		}
		return obj;
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		return JsonUtil.toBean((String) get(key), clazz);
	}

	protected abstract Serializable get0(String key);

}
