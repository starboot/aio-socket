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
package cn.starboot.socket.utils.guava;

import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GuavaUtils {

	private static final Logger log = LoggerFactory.getLogger(GuavaUtils.class);

	/**
	 *
	 * @param concurrencyLevel
	 * @param timeToLiveSeconds 设置写缓存后过期时间（单位：秒）
	 * @param timeToIdleSeconds 设置读缓存后过期时间（单位：秒）
	 * @param initialCapacity
	 * @param maximumSize
	 * @param recordStats
	 * @return
	 */
	public static <K, V> LoadingCache<K, V> createLoadingCache(Integer concurrencyLevel, Long timeToLiveSeconds, Long timeToIdleSeconds, Integer initialCapacity,
															   Integer maximumSize, boolean recordStats) {
		return createLoadingCache(concurrencyLevel, timeToLiveSeconds, timeToIdleSeconds, initialCapacity, maximumSize, recordStats, null);
	}

	/**
	 *
	 * @param concurrencyLevel
	 * @param timeToLiveSeconds 设置写缓存后过期时间（单位：秒）
	 * @param timeToIdleSeconds 设置读缓存后过期时间（单位：秒）
	 * @param initialCapacity
	 * @param maximumSize
	 * @param recordStats
	 * @param removalListener
	 * @return
	 */
	public static <K, V> LoadingCache<K, V> createLoadingCache(Integer concurrencyLevel, Long timeToLiveSeconds, Long timeToIdleSeconds, Integer initialCapacity,
	        Integer maximumSize, boolean recordStats, RemovalListener<K, V> removalListener) {

		if (removalListener == null) {
			removalListener = new RemovalListener<K, V>() {
				@Override
				public void onRemoval(RemovalNotification<K, V> notification) {
					log.info(notification.getKey() + " was removed");
				}
			};
		}

		CacheBuilder<K, V> cacheBuilder = CacheBuilder.newBuilder().removalListener(removalListener);

		//设置并发级别为8，并发级别是指可以同时写缓存的线程数
		cacheBuilder.concurrencyLevel(concurrencyLevel);
		if (timeToLiveSeconds != null && timeToLiveSeconds > 0) {
			//设置写缓存后8秒钟过期
			cacheBuilder.expireAfterWrite(timeToLiveSeconds, TimeUnit.SECONDS);
		}
		if (timeToIdleSeconds != null && timeToIdleSeconds > 0) {
			//设置访问缓存后8秒钟过期
			cacheBuilder.expireAfterAccess(timeToIdleSeconds, TimeUnit.SECONDS);
		}

		//设置缓存容器的初始容量为10
		cacheBuilder.initialCapacity(initialCapacity);
		//设置缓存最大容量为100，超过100之后就会按照LRU最近最少使用算法来移除缓存项
		cacheBuilder.maximumSize(maximumSize);

		if (recordStats) {
			//设置要统计缓存的命中率
			cacheBuilder.recordStats();
		}
		//build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
		LoadingCache<K, V> loadingCache = cacheBuilder.build(new CacheLoader<K, V>() {
			@Override
			public V load(K key) throws Exception {
				return null;
			}
		});
		return loadingCache;

		//		for (int i = 0; i < 20; i++)
		//		{
		//			//从缓存中得到数据，由于我们没有设置过缓存，所以需要通过CacheLoader加载缓存数据
		//			Long student = studentCache.get("p");
		//			System.out.println(student);
		//			//休眠1秒
		//			TimeUnit.SECONDS.sleep(1);
		//		}

		//		System.out.println("cache stats:");
		//最后打印缓存的命中率等 情况
		//		System.out.println(studentCache.stats().toString());
	}

	public GuavaUtils() {

	}
}
