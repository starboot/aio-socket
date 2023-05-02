package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 *  新增一个或多个key-Map  :  3
 *  获取一个key-Map  :  v1
 *  获取多个key-Map  :  [v1, v2]
 *  获取全部key-Map  :  {k3=v3, k1=v1, k2=v2}
 *  查看当前hash的元素个数  :  3
 *  删除hash指定的key字段，对应的value也会被删除  :  1
 * {k3=v3, k1=v1}
 *  获取当前hash的所有value  :  [v3, v1]
 *  获取当前hash的所有key  :  [k3, k1]
 *  判断当前hash中指定key是否存在 :  true
 *  判断当前hash中指定key是否存在  :  false
 * ---------------------------------
 * {k=0}
 *  设置增量 +5 :  5
 * {k=5}
 *  设置增量 -2 :  3
 * {k=3}
 * ---------------------------------
 * {k1=v1, k2=v2}
 *  判断当前hash中是否存在此key，不存在即创建，存在创建失败  :  1
 *  判断当前hash中是否存在此key，不存在即创建，存在创建失败  :  0
 *
 * Process finished with exit code 0
 */
public class JedisHashTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(7);
		jedis.flushDB();
		Map<String, String> map = new HashMap<String, String>();
		map.put("k1", "v1");
		map.put("k2", "v2");
		map.put("k3", "v3");
		System.out.println(" 新增一个或多个key-Map  :  "+jedis.hset("hash", map));
		System.out.println(" 获取一个key-Map  :  "+jedis.hget("hash", "k1"));
		System.out.println(" 获取多个key-Map  :  "+jedis.hmget("hash", "k1","k2"));
		System.out.println(" 获取全部key-Map  :  "+jedis.hgetAll("hash"));
		System.out.println(" 查看当前hash的元素个数  :  "+jedis.hlen("hash"));
		System.out.println(" 删除hash指定的key字段，对应的value也会被删除  :  "+jedis.hdel("hash", "k2"));
		System.out.println(jedis.hgetAll("hash"));
		System.out.println(" 获取当前hash的所有value  :  "+jedis.hvals("hash"));
		System.out.println(" 获取当前hash的所有key  :  "+jedis.hkeys("hash"));
		System.out.println(" 判断当前hash中指定key是否存在 :  "+jedis.hexists("hash", "k1"));
		System.out.println(" 判断当前hash中指定key是否存在  :  "+jedis.hexists("hash", "k2"));
		System.out.println("---------------------------------");
		jedis.flushDB();
		Map<String,String> m = new HashMap<String, String>();
		m.put("k","0");
		jedis.hset("ha",m);
		System.out.println(jedis.hgetAll("ha"));
		System.out.println(" 设置增量 +5 :  "+jedis.hincrBy("ha", "k", 5));
		System.out.println(jedis.hgetAll("ha"));
		System.out.println(" 设置增量 -2 :  "+jedis.hincrBy("ha", "k", -2));
		System.out.println(jedis.hgetAll("ha"));
		System.out.println("---------------------------------");
		jedis.flushDB();
		jedis.hset("has", "k1", "v1");
		jedis.hset("has", "k2", "v2");
		System.out.println(jedis.hgetAll("has"));
		System.out.println(" 判断当前hash中是否存在此key，不存在即创建，存在创建失败  :  "+jedis.hsetnx("has", "k3", "v3"));
		System.out.println(" 判断当前hash中是否存在此key，不存在即创建，存在创建失败  :  "+jedis.hsetnx("has", "k3", "v3"));


	}
}
