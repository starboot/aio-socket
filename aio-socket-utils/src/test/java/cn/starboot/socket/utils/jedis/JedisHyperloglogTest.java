package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;

/**
 *   创建第一组元素  :  1
 *   统计第一组元素数量  :  4
 *   创建第二组元素 :  1
 *   统计第二组元素数量  :  4
 *   合并第一组和第二组数据  :  OK
 *   合并后key3的元素数量  :  6
 *
 * redis-hyperloglog 基数统计的算法 （0.81%错误率），Redis 在 2.8.9 版本添加了 HyperLogLog 结构；
 * 优点：占用的内存是固定的，2^64 不同元素的基数，只占用12kb内存；
 */
public class JedisHyperloglogTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(10);
		jedis.flushDB();
		System.out.println("  创建第一组元素  :  "+jedis.pfadd("hylog", "a", "b", "c", "d"));
		System.out.println("  统计第一组元素数量  :  "+jedis.pfcount("hylog"));
		System.out.println("  创建第二组元素 :  "+jedis.pfadd("hylogTwo", "c", "d", "e", "f"));
		System.out.println("  统计第二组元素数量  :  "+jedis.pfcount("hylogTwo"));
		System.out.println("  合并第一组和第二组数据  :  "+jedis.pfmerge("key", "hylog", "hylogTwo"));
		System.out.println("  合并后key3的元素数量  :  "+jedis.pfcount("key"));
	}
}
