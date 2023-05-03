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

import cn.starboot.socket.utils.QuickTimerTask;
import cn.starboot.socket.utils.lock.SetWithLock;
import cn.starboot.socket.utils.lock.WriteLockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时更新redis的过期时间
 *
 * @author MDong
 */
public class RedisExpireUpdateTask extends QuickTimerTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisExpireUpdateTask.class);

	private static boolean started = false;

	private static RedisExpireUpdateTask redisExpireUpdateTask;

	private static final SetWithLock<ExpireEntity> setWithLock = new SetWithLock<>(new HashSet<>());

	public synchronized static RedisExpireUpdateTask getInstance(Integer seconds) {
		long mills = TimeUnit.SECONDS.toMillis(seconds);
		if (Objects.isNull(redisExpireUpdateTask)) {
			redisExpireUpdateTask = new RedisExpireUpdateTask(mills, mills);
			started = true;
		}
		return redisExpireUpdateTask;
	}

	private RedisExpireUpdateTask(long delay, long period) {
		super(delay, period);
	}

	public boolean getStatus() {
		return started;
	}

	public void add(String cacheName, String key, String value) {
		setWithLock.add(new ExpireEntity(cacheName, key, value));
	}

	@Override
	public void run() {
		setWithLock.handle((WriteLockHandler<Set<ExpireEntity>>) expireVos -> {
			for (ExpireEntity expireEntity : expireVos) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("更新缓存过期时间, cacheName:{}, key:{}",
							expireEntity.getCacheName(),
							expireEntity.getKey());
				}
				RedisCache cache = RedisCache.getCache(expireEntity.getCacheName());
				if (cache.ttl(expireEntity.getKey()) > 0) {
					cache.put(expireEntity.getKey(), expireEntity.getValue());
				}
			}
			expireVos.clear();
		});
	}
}
