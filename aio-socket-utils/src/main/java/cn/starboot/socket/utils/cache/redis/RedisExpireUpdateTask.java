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
import org.redisson.api.RBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 定时更新redis的过期时间
 */
public class RedisExpireUpdateTask {

	private static final Logger log = LoggerFactory.getLogger(RedisExpireUpdateTask.class);

	private static boolean started = false;

	private static final Set<ExpireVo> set = new HashSet<>();

	private static final SetWithLock<ExpireVo> setWithLock = new SetWithLock<>(set);

	public static void add(String cacheName, String key, long expire) {
		ExpireVo expireVo = new ExpireVo(cacheName, key, expire);
		setWithLock.add(expireVo);
	}

	public static void start() {
		if (started) {
			return;
		}
		synchronized (RedisExpireUpdateTask.class) {
			if (started) {
				return;
			}
			started = true;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					WriteLock writeLock = setWithLock.writeLock();
					writeLock.lock();
					try {
						Set<ExpireVo> set = setWithLock.getObj();
						for (ExpireVo expireVo : set) {
							log.debug("更新缓存过期时间, cacheName:{}, key:{}, expire:{}", expireVo.getCacheName(), expireVo.getKey(), expireVo.getTimeToIdleSeconds());

							RedisCache redisCache = RedisCache.getCache(expireVo.getCacheName());
							RBucket<Serializable> bucket = redisCache.getBucket(expireVo.getKey());
							bucket.expireAsync(expireVo.getTimeToIdleSeconds(), TimeUnit.SECONDS);
						}
						set.clear();
					} catch (Throwable e) {
						log.error(e.getMessage(), e);
					} finally {
						writeLock.unlock();
						try {
							Thread.sleep(1000 * 10);
						} catch (InterruptedException e) {
							log.error(e.toString(), e);
						}
					}
				}

			}
		}, RedisExpireUpdateTask.class.getName()).start();
	}

	private RedisExpireUpdateTask() {

	}
}
