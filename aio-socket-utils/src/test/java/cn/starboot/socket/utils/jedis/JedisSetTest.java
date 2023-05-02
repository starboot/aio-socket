package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;

/**
 *   给集合中添加元素  : 3
 *   查看指定集合的所有值  set  : [ccc, bbb, aaa]
 *   判断某一个值是否在此集合中  : true
 *   判断某一个值是否在此集合中  : false
 *   查看集合中的元素个数  : 3
 *   移除集合中指定元素  : 1
 *   随机抽选元素（个数可指定）  : [ccc, aaa]
 *   随机删除集合中的某些元素，可指定多个 : [ccc, aaa]
 * -------------------------------------
 *   s1  : [c1, b1, a1]  s2  : [c2, b2, a2]
 *   将一个指定的值移动到另一个集合中  : 1
 *   s1  : [c1, b1]  s2  : [c2, b2, a1, a2]
 * -------------------------------------
 *   k1  : [v3, v1, v2]  k2  : [v4, v3, v2]
 *  取两个集合的差集  : [v1]
 *  取两个集合的交集  : [v3, v2]
 *  取两个集合的并集  : [v3, v1, v2, v4]
 *
 * Process finished with exit code 0
 */
public class JedisSetTest {
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(6);
		jedis.flushDB();
		System.out.println("  给集合中添加元素  : "+jedis.sadd("set", "aaa", "bbb", "ccc"));
		System.out.println("  查看指定集合的所有值  set  : "+jedis.smembers("set"));
		System.out.println("  判断某一个值是否在此集合中  : "+jedis.sismember("set", "aaa"));
		System.out.println("  判断某一个值是否在此集合中  : "+jedis.sismember("set", "ddd"));
		System.out.println("  查看集合中的元素个数  : "+jedis.scard("set"));
		System.out.println("  移除集合中指定元素  : "+jedis.srem("set", "bbb"));
		System.out.println("  随机抽选元素（个数可指定）  : "+jedis.srandmember("set",2));
		System.out.println("  随机删除集合中的某些元素，可指定多个 : "+jedis.spop("set", 2));
		System.out.println("-------------------------------------");
		jedis.flushDB();
		jedis.sadd("s1","a1","b1","c1");
		jedis.sadd("s2","a2","b2","c2");
		System.out.println("  s1  : "+jedis.smembers("s1")+"  s2  : "+jedis.smembers("s2"));
		System.out.println("  将一个指定的值移动到另一个集合中  : "+jedis.smove("s1", "s2", "a1"));
		System.out.println("  s1  : "+jedis.smembers("s1")+"  s2  : "+jedis.smembers("s2"));
		System.out.println("-------------------------------------");
		jedis.flushDB();
		jedis.sadd("k1","v1","v2","v3");
		jedis.sadd("k2","v2","v3","v4");
		System.out.println("  k1  : "+jedis.smembers("k1")+"  k2  : "+jedis.smembers("k2"));
		System.out.println(" 取两个集合的差集  : "+jedis.sdiff("k1", "k2"));
		System.out.println(" 取两个集合的交集  : "+jedis.sinter("k1", "k2"));
		System.out.println(" 取两个集合的并集  : "+jedis.sunion("k1", "k2"));
	}
}
