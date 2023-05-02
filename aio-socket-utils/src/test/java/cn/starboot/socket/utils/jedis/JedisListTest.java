package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.ListPosition;

/**
 * 将一个值或者多个值插入列表头(左边)部 k1  :3
 * 获取列表的值(可获取列表区间的值) k1  :[v3, v2, v1]
 *  将一个值或者多个值插入列表尾(右边)部 s1  :3
 * 获取列表的值(可获取列表区间的值) s1  :[a1, a2, a3]
 * 移除列表的元素，从左边开始  :a1
 * s1  :[a2, a3]
 * 移除列表的元素，从右边开始  :v1
 * k1   :[v3, v2]
 * 获取列表索引处元素  k1   0   :v3
 * 获取列表索引处元素  s1   1   :a3
 *  获取列表的长度   k1  :2
 * 移除指定个数的value，精确匹配,删除1个  :1
 * 添加元素 k1  :6
 * 获取列表的值(可获取列表区间的值) k1  :[v3, v2, v2, v2, v1, v3]
 * 移除指定个数的value，精确匹配,删除多个  :2
 * 获取列表的值(可获取列表区间的值) k1  :[v3, v2, v1, v3]
 * 截取元素区间的值  :OK
 * 获取列表的值(可获取列表区间的值) s1  :[a2, a3]
 * -------------------------------------
 * 3
 * [three, two, one]
 * 移除列表的最后一个元素，将它移动到新的列表中   :one
 * [three, two]
 * [one]
 * -------------------------------------
 * 3
 * [three, two, one]
 * 将列表指定下标替换为另一个值  :OK
 * [three, four, one]
 * 将某一个具体的值插入到列表中某个元素的前面  :4
 * [aaa, three, four, one]
 * 将某一个具体的值插入到列表中某个元素的后面  :5
 * [aaa, three, ddd, four, one]
 *
 * Process finished with exit code 0
 */
public class JedisListTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(5);
		jedis.flushDB();
		System.out.println("将一个值或者多个值插入列表头(左边)部 k1  :" + jedis.lpush("k1", "v1", "v2", "v3"));
		System.out.println("获取列表的值(可获取列表区间的值) k1  :" + jedis.lrange("k1", 0, -1));
		System.out.println(" 将一个值或者多个值插入列表尾(右边)部 s1  :" + jedis.rpush("s1", "a1", "a2", "a3"));
		System.out.println("获取列表的值(可获取列表区间的值) s1  :" + jedis.lrange("s1", 0, -1));
		System.out.println("移除列表的元素，从左边开始  :" + jedis.lpop("s1"));
		System.out.println("s1  :" + jedis.lrange("s1", 0, -1));
		System.out.println("移除列表的元素，从右边开始  :" + jedis.rpop("k1"));
		System.out.println("k1   :" + jedis.lrange("k1", 0, -1));
		System.out.println("获取列表索引处元素  k1   0   :"+ jedis.lindex("k1", 0));
		System.out.println("获取列表索引处元素  s1   1   :" + jedis.lindex("s1", 1));
		System.out.println(" 获取列表的长度   k1  :" + jedis.llen("k1"));
		System.out.println("移除指定个数的value，精确匹配,删除1个  :" + jedis.lrem("k1", 1, "v2"));
		System.out.println("添加元素 k1  :" + jedis.lpush("k1", "v1", "v2", "v2", "v2", "v3"));
		System.out.println("获取列表的值(可获取列表区间的值) k1  :" + jedis.lrange("k1", 0, -1));
		System.out.println("移除指定个数的value，精确匹配,删除多个  :" + jedis.lrem("k1", 2, "v2"));
		System.out.println("获取列表的值(可获取列表区间的值) k1  :" + jedis.lrange("k1", 0, -1));
		System.out.println("截取元素区间的值  :" + jedis.ltrim("s1", 0, 1));
		System.out.println("获取列表的值(可获取列表区间的值) s1  :" + jedis.lrange("s1", 0, -1));
		jedis.flushDB();
		System.out.println(jedis.lpush("li", "one", "two", "three"));
		System.out.println( jedis.lrange("li", 0, -1));
		System.out.println("移除列表的最后一个元素，将它移动到新的列表中   :" + jedis.rpoplpush("li", "ki"));
		System.out.println( jedis.lrange("li", 0, -1));
		System.out.println(jedis.lrange("ki", 0, -1));
		jedis.flushDB();
		System.out.println(jedis.lpush("list", "one", "two", "three"));
		System.out.println( jedis.lrange("list", 0, -1));
		System.out.println("将列表指定下标替换为另一个值  :" + jedis.lset("list", 1, "four"));
		System.out.println( jedis.lrange("list", 0, -1));
		System.out.println("将某一个具体的值插入到列表中某个元素的前面  :" + jedis.linsert("list", ListPosition.BEFORE, "three", "aaa"));
		System.out.println(jedis.lrange("list", 0, -1));
		System.out.println("将某一个具体的值插入到列表中某个元素的后面  :" + jedis.linsert("list", ListPosition.AFTER, "three", "ddd"));
		System.out.println(jedis.lrange("list", 0, -1));
		jedis.flushDB();

	}
}
