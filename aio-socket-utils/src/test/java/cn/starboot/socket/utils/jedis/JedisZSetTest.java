package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;

/**
 *    添加元素  :  1
 *    查看元素，默认从小到大 :  [aaa, bbb, ccc]
 *    查看元素，从大到小排序 :  [ccc, bbb, aaa]
 * Double.NEGATIVE_INFINITY == 负无穷 || Double.POSITIVE_INFINITY == 正无穷
 *    根据当前Zset的key显示元素 [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]:  [aaa, bbb, ccc]
 *    根据当前Zset的key显示元素 [Double.NEGATIVE_INFINITY, 120]:  [aaa, bbb]
 * -------------------------------------------
 * [erwrwerw, sadasdad, sdsad, qwqwqs, cdcdc]
 *   删除当前Zset的指定的值  :  1
 * [erwrwerw, sadasdad, sdsad, cdcdc]
 *   当前Zset中元素的个数  :  4
 *   获取当前Zset的key在某个区间内的个数  :  3
 *
 * Process finished with exit code 0
 */
public class JedisZSetTest {
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost",6379);
		jedis.select(8);
		jedis.flushDB();
		jedis.zadd("zset", 100, "aaa");
		jedis.zadd("zset", 120, "bbb");
		System.out.println("   添加元素  :  "+jedis.zadd("zset", 150, "ccc"));
		System.out.println("   查看元素，默认从小到大 :  "+jedis.zrange("zset", 0, -1));
		System.out.println("   查看元素，从大到小排序 :  "+jedis.zrevrange("zset", 0, -1));
		System.out.println("Double.NEGATIVE_INFINITY == 负无穷 || Double.POSITIVE_INFINITY == 正无穷");
		System.out.println("   根据当前Zset的key显示元素 [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]:  "+jedis.zrangeByScore("zset", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
		System.out.println("   根据当前Zset的key显示元素 [Double.NEGATIVE_INFINITY, 120]:  "+jedis.zrangeByScore("zset", Double.NEGATIVE_INFINITY, 120));
		System.out.println("-------------------------------------------");
		jedis.flushDB();
		jedis.zadd("z",89,"sadasdad");
		jedis.zadd("z",105,"qwqwqs");
		jedis.zadd("z",100,"sdsad");
		jedis.zadd("z",132,"cdcdc");
		jedis.zadd("z",77,"erwrwerw");
		System.out.println(jedis.zrange("z", 0, -1));
		System.out.println("  删除当前Zset的指定的值  :  "+jedis.zrem("z", "qwqwqs"));
		System.out.println(jedis.zrange("z", 0, -1));
		System.out.println("  当前Zset中元素的个数  :  "+jedis.zcard("z"));
		System.out.println("  获取当前Zset的key在某个区间内的个数  :  "+jedis.zcount("z", 50, 100));
		jedis.flushDB();

	}
}
