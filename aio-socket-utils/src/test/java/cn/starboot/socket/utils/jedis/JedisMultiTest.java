package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * //运行结果：
 * [v1, v2]
 */
public class JedisMultiTest {

	public static void main(String[] args) {
		TestSucc();
	}

	// 正常示例  [v1, v2]
	private static void TestSucc() {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(12);
		jedis.flushDB();
		Transaction multi = jedis.multi(); //开启事务
		try {
			multi.set("key", "value");
			multi.mset("k1", "v1","k2","v2");
			multi.exec();// 执行事务
		}catch (Exception e){
			multi.discard(); //放弃事务
			e.printStackTrace();
		}finally {
			System.out.println(jedis.mget("k1", "k2"));
			jedis.close();
		}
		jedis.flushDB();
	}


	// 异常示例  [null, null]
	private static void TestFail() {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(12);
		jedis.flushDB();
		Transaction multi = jedis.multi(); //开启事务
		try {
			multi.set("key", "value");
			multi.mset("k1", "v1","k2","v2");
			String a = null;
			a.equals("aaa"); // 代码抛出异常，事务执行失败！
			multi.exec();// 执行事务
		}catch (Exception e){
			multi.discard(); //放弃事务
			e.printStackTrace();
		}finally {
			System.out.println(jedis.mget("k1", "k2"));
			jedis.close();
		}
		jedis.flushDB();
	}
}
