package cn.starboot.socket.core.utils.jedis;

import redis.clients.jedis.Jedis;

/**
 * 添加一个key:  OK
 * 同时新增多个值:  OK
 * 获取当前key的value:  v3
 * 同时获取多个值:  [aaaaa, v4]
 * 查看所有key:  [k3, k4, k5, k1, k2]
 * 追加字符串，若当前key不存在，相当于新增当前key(set操作)
 * 追加字符串:  11
 *  查看当前key的value的长度:  11
 * 追加字符串:  10
 * 同时获取多个值:  [aaaaassbbsb, ssasdadsda]
 * -------------------------------------
 * OK
 * incr 自增操作相当于 i++
 * 1
 * decr 自减操作相当于 i--
 * 0
 * incrby 设置步长，相当于 i+=n
 * 10
 * decrby 设置步长，相当于 i-=n
 * 5
 * k1:  v1dasdad      k2v2aaa
 * 截取字符串:  v1da
 * 如果结束端点为负值的话，为获取全部字符串:  v1dasdad
 * 替换字符串:  5
 *  k2:  dasda
 * 替换字符串:  12
 *  k2:  dasdadsadsad
 * 当前key不存在时创建:  1
 * 当前key不存在时创建:  0
 * 设置过期时间:  OK
 * 同时处理多个,当前key不存在时创建:  1
 * 同时处理多个,当前key不存在时创建:  0
 * -------------------------------------
 * getset 先get后set  1.如果当前key不存在，先返回null，然后创建2.如果当前key存在，先返回当前value，然后重新设置当前key的值
 * null
 * aaaaa
 * bbbbb
 *
 * @author https://blog.csdn.net/weixin_44890938/article/details/119183649
 */
public class JedisStringTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(4);
		jedis.flushDB();
		System.out.println("添加一个key:  " + jedis.set("k1", "aaaaa"));
		System.out.println("同时新增多个值:  " + jedis.mset("k2", "v2", "k3", "v3", "k4", "v4", "k5", "v5"));
		System.out.println("获取当前key的value:  " + jedis.get("k3"));
		System.out.println("同时获取多个值:  " + jedis.mget("k1", "k4"));
		System.out.println("查看所有key:  " + jedis.keys("*"));
		System.out.println("追加字符串，若当前key不存在，相当于新增当前key(set操作)");
		System.out.println("追加字符串:  " + jedis.append("k1", "ssbbsb"));
		System.out.println(" 查看当前key的value的长度:  " + jedis.strlen("k1"));
		System.out.println("追加字符串:  " + jedis.append("k6", "ssasdadsda"));
		System.out.println("同时获取多个值:  " + jedis.mget("k1", "k6"));
		jedis.flushDB();
		System.out.println("-------------------------------------");
		jedis.mset("k2", "v2aaa", "k1", "v1dasdad");
		System.out.println(jedis.set("kk", "0"));
		System.out.println("incr 自增操作相当于 i++");
		System.out.println(jedis.incr("kk"));
		System.out.println("decr 自减操作相当于 i--");
		System.out.println(jedis.decr("kk"));
		System.out.println("incrby 设置步长，相当于 i+=n");
		System.out.println(jedis.incrBy("kk", 10));
		System.out.println("decrby 设置步长，相当于 i-=n");
		System.out.println(jedis.decrBy("kk", 5));
		System.out.println("k1:  " + jedis.get("k1") + "      k2" + jedis.get("k2"));
		System.out.println("截取字符串:  " + jedis.getrange("k1", 0, 3));
		System.out.println("如果结束端点为负值的话，为获取全部字符串:  " + jedis.getrange("k1", 0, -1));
		System.out.println("替换字符串:  " + jedis.setrange("k2", 0, "dasd"));
		System.out.println(" k2:  " + jedis.get("k2"));
		System.out.println("替换字符串:  " + jedis.setrange("k2", 0, "dasdadsadsad"));
		System.out.println(" k2:  " + jedis.get("k2"));
		System.out.println("当前key不存在时创建:  " + jedis.setnx("k", "sadada"));
		System.out.println("当前key不存在时创建:  " + jedis.setnx("k2", "sadada"));
		System.out.println("设置过期时间:  " + jedis.setex("as", 5, "dasdasda"));
		System.out.println("同时处理多个,当前key不存在时创建:  " + jedis.msetnx("k3", "v3", "k4", "v4"));
		System.out.println("同时处理多个,当前key不存在时创建:  " + jedis.msetnx("k3", "v3", "k5", "v5"));
		jedis.flushDB();
		System.out.println("-------------------------------------");
		System.out.println("getset 先get后set  " +
				"1.如果当前key不存在，先返回null，然后创建" +
				"2.如果当前key存在，先返回当前value，然后重新设置当前key的值");
		System.out.println(jedis.getSet("v1", "aaaaa"));
		System.out.println(jedis.getSet("v1", "bbbbb"));
		System.out.println(jedis.get("v1"));
		jedis.flushDB();

	}
}
