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
package cn.starboot.socket.core.utils.cache.redis;

import cn.starboot.socket.core.utils.TimerService;
import cn.starboot.socket.core.utils.concurrent.collection.ConcurrentWithSet;
import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 定时更新redis的过期时间
 *
 * 对缓存的封装，包括Redis、Caffeine和j2cache，
 * 此部分源代码出自talent-tan的开源项目t-io。
 *
 * @author t-io: https://gitee.com/tywo45/t-io.git
 * @author MDong
 */
public class RedisExpireUpdateService extends TimerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisExpireUpdateService.class);

	private static boolean started = false;

	private static RedisExpireUpdateService redisExpireUpdateTask;

	private static final ConcurrentWithSet<ExpireEntity> setWithLock = new ConcurrentWithSet<>(new HashSet<>());

	public synchronized static RedisExpireUpdateService getInstance(Integer seconds) {
		long mills = TimeUnit.SECONDS.toMillis(seconds);
		if (Objects.isNull(redisExpireUpdateTask)) {
			redisExpireUpdateTask = new RedisExpireUpdateService(mills, mills);
			started = true;
		}
		return redisExpireUpdateTask;
	}

	private RedisExpireUpdateService(long delay, long period) {
		super(delay, period);
	}

	public boolean getStatus() {
		return started;
	}

	public void add(String cacheName, String key, String value) {
		setWithLock.add(new ExpireEntity(cacheName, key, value), new Consumer<Boolean>() {
			@Override
			public void accept(Boolean aBoolean) {

			}
		});
	}

	@Override
	public void run() {
		setWithLock.handle(new ConcurrentWithWriteHandler<Set<ExpireEntity>>() {
			@Override
			public void handler(Set<ExpireEntity> expireEntities) throws Exception {
				for (ExpireEntity expireEntity : expireEntities) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("update cache live time, cacheName:{}, key:{}",
								expireEntity.getCacheName(),
								expireEntity.getKey());
					}
					RedisCache cache = RedisCache.getCache(expireEntity.getCacheName());
					if (cache.ttl(expireEntity.getKey()) > 0) {
						cache.put(expireEntity.getKey(), expireEntity.getValue());
					}
				}
				expireEntities.clear();
			}
		});
	}
}
