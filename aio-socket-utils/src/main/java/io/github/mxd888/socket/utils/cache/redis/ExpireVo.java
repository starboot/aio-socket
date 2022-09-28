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
package io.github.mxd888.socket.utils.cache.redis;

import java.util.Objects;

/**
 * @author tanyaowu
 * 2017年8月14日 下午1:40:14
 */
public class ExpireVo {

	private String cacheName;

	private String key;

	private long timeToIdleSeconds;

	public ExpireVo(String cacheName, String key, long timeToIdleSeconds) {
		super();
		this.cacheName = cacheName;
		this.key = key;
		this.timeToIdleSeconds = timeToIdleSeconds;
		//		this.expirable = expirable;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExpireVo other = (ExpireVo) obj;

		return Objects.equals(cacheName, other.cacheName) && Objects.equals(key, other.key);
	}

	public String getCacheName() {
		return cacheName;
	}

	public long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public String getKey() {
		return key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cacheName == null ? 0 : cacheName.hashCode());
		result = prime * result + (key == null ? 0 : key.hashCode());
		return result;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public void setTimeToIdleSeconds(long expire) {
		this.timeToIdleSeconds = expire;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
