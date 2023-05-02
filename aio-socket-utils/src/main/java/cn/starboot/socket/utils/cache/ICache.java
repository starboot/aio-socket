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

import java.io.Serializable;

/**
 * 对缓存进行封装
 *
 * @author MDong
 */
public interface ICache {

	/**
	 * 有时候需要放一个空对象到缓存中
	 */
	class NullClass implements Serializable {
		private static final long serialVersionUID = -2298613658358477523L;
	}

	/**
	 * 用于临时存放于缓存中的对象，防止缓存null攻击
	 */
	NullClass NULL_OBJ = new NullClass();

	/**
	 * 在本地最大的过期时间，这样可以防止内存爆掉，单位：秒
	 */
	int MAX_EXPIRE_IN_LOCAL = 10 * 60;

	String getCacheName();

	void clear();

	Serializable get(String key);

	<T> T get(String key, Class<T> clazz);

	Iterable<String> keys();

	void put(String key, Object value);

	void remove(String key);

	void putTemporary(String key, Object value);

	/**
	 * 对象还会存活多久。
	 * @return currTime in milliseconds
	 *          -2 if the key does not exist.
	 *          -1 if the key exists but has no associated expire.
	 */
	long ttl(String key);

	Long getTimeToLiveSeconds();

	Long getTimeToIdleSeconds();

//	<T> T getCache();
}
