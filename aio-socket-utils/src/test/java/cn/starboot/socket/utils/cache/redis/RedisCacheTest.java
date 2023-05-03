package cn.starboot.socket.utils.cache.redis;

import cn.starboot.socket.utils.cache.TestObj;
import cn.starboot.socket.utils.cache.redis.RedisCache;
import redis.clients.jedis.Jedis;

/**
 * 测试cache封装效果
 */
public class RedisCacheTest {

	public static void main(String[] args) throws InterruptedException {

		testRedis();
	}


	private static void testRedis() throws InterruptedException {
		Jedis jedis = new Jedis("localhost", 6379);
		RedisCache testCache = RedisCache.register(jedis, "TestCache", 60L, 30L, 10);
		RedisCache testCacheY = RedisCache.register(jedis, "TestCacheY", 60L, 30L);
		testCache.put("testKey", new TestObj("mxd", "男", "哈哈哈哈"));

		testCache.put("testKey11", new TestObj("mxd", "男", "哈哈哈哈111"));

		System.out.println(testCache.getTimeout());
		Thread.sleep(5000);
		System.out.println(testCache.ttl("testKey11"));

		TestObj testKey11 = testCache.get("testKey11", TestObj.class);

		System.out.println(testKey11);

		System.out.println(testCache.keys());

		testCache.remove("testKey");

		Thread.sleep(1000);
		System.out.println(testCache.keys());

		testCacheY.put("key1", new TestObj("mxd", "男", "哈哈哈哈"));
		testCacheY.put("key2", new TestObj("mxd", "男1", "哈哈哈哈"));

		Thread.sleep(10000);

		System.out.println(testCache.ttl("testKey11"));

		TestObj testKey111 = testCache.get("testKey11", TestObj.class);

		System.out.println(testKey111);

		Thread.sleep(5000);
	}
}
