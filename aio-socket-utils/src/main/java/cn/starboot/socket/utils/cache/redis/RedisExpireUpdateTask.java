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
package cn.starboot.socket.utils.cache.redis;

import cn.starboot.socket.utils.lock.SetWithLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 定时更新redis的过期时间
 */
public class RedisExpireUpdateTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisExpireUpdateTask.class);

	private static boolean started = false;

	private static final SetWithLock<ExpireVo> setWithLock = new SetWithLock<>(new HashSet<>());

	public static void add(String cacheName, String key, String value, long expire) {
		ExpireVo expireVo = new ExpireVo(cacheName, key, value, expire);
		setWithLock.add(expireVo);
	}

	public static void start(Long millis) {
		if (started || Objects.isNull(millis) || millis <= 0L) {
			return;
		}
		synchronized (RedisExpireUpdateTask.class) {
			if (started) {
				return;
			}
			started = true;
		}

		new Thread(() -> {
			while (true) {
				WriteLock writeLock = setWithLock.writeLock();
				writeLock.lock();
				try {
					Set<ExpireVo> set = setWithLock.getObj();
					for (ExpireVo expireVo : set) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("更新缓存过期时间, cacheName:{}, key:{}, expire:{}",
									expireVo.getCacheName(),
									expireVo.getKey(),
									expireVo.getTimeToIdleSeconds());
						}
						RedisCache cache = RedisCache.getCache(expireVo.getCacheName());
						if (cache.ttl(expireVo.getKey()) > 0) {
							cache.put(expireVo.getKey(), expireVo.getValue());
						}
					}
					set.clear();
				} catch (Throwable e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					writeLock.unlock();
					try {
						Thread.sleep(millis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}, RedisExpireUpdateTask.class.getName()).start();
	}

	public static boolean getStatus() {
		return started;
	}

	private RedisExpireUpdateTask() {

	}
}
