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
package cn.starboot.socket.core.utils.cache.caffeine;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultRemovalListener
 *
 * @param <K>
 * @param <V>
 * @author t-io: https://gitee.com/tywo45/t-io.git
 * @author MDong
 */
public class DefaultRemovalListener<K, V> implements RemovalListener<K, V> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRemovalListener.class);

	private final String cacheName;

	public DefaultRemovalListener(String cacheName) {
		this.cacheName = cacheName;
	}

	@Override
	public void onRemoval(K key, V value, RemovalCause cause) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("cacheName:{}, key:{}, value:{} was removed", cacheName, key, value);
		}
	}
}
