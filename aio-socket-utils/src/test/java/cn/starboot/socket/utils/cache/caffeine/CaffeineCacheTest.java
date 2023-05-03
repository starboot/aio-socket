package cn.starboot.socket.utils.cache.caffeine;

import cn.starboot.socket.utils.cache.TestObj;

public class CaffeineCacheTest {

	public static void main(String[] args) {
		CaffeineCache testCaffeineCache = CaffeineCache.register("TestCaffeineCache", 100L, 100L);

		testCaffeineCache.put("testKey", new TestObj("mxd", "男", "哈哈哈哈"));

		testCaffeineCache.put("testKey11", new TestObj("mxd", "男", "哈哈哈哈111"));

		System.out.println(testCaffeineCache.keys());

		System.out.println(testCaffeineCache.size());

		System.out.println(testCaffeineCache.asMap());

//		System.out.println(testCaffeineCache.ttl("testKey11"));

		System.out.println(testCaffeineCache.get("testKey11"));

		testCaffeineCache.remove("testKey11");

		System.out.println(testCaffeineCache.keys());
	}
}
