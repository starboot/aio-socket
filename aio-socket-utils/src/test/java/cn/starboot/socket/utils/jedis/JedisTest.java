package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;

/**
 * 查看连接状态：PONG
 * 切换数据库：OK
 * 新增一个key：OK
 * 查看数据大小：1
 * 查看当前数据库所有 key：[aa]
 * 清空当前数据库：OK
 * 清空所有数据库：OK
 * 新增一个key：OK
 * 新增一个key：OK
 * 新增一个key：OK
 * 查看当前key是否存在：true
 * 查看当前key是否存在：false
 * 查看当前数据库所有 key：[b, s, k]
 * 设置当前key过期时间：1
 * 休眠 2S 后查看当前key剩余过期时间：3
 * 再次休眠 2S 后查看当前key剩余过期时间：1
 * 移除当前的key：1
 * 查看当前数据库所有 key：[s, k]
 * 查看当前key的类型：string
 * 清空当前数据库：OK
 *
 * @author https://blog.csdn.net/weixin_44890938/article/details/119183649
 */
public class JedisTest {
	public synchronized static void main(String[] args) throws InterruptedException {

		Jedis jedis = new Jedis("localhost", 6379);
		System.out.println("查看连接状态：" + jedis.ping());
		System.out.println("切换数据库：" + jedis.select(2));
		System.out.println("新增一个key：" + jedis.set("aa", "sasa"));
		System.out.println("查看数据大小：" + jedis.dbSize());
		System.out.println("查看当前数据库所有 key：" + jedis.keys("*"));
		System.out.println("清空当前数据库：" + jedis.flushDB());
		System.out.println("清空所有数据库：" + jedis.flushAll());
		System.out.println("新增一个key：" + jedis.set("k", "v"));
		System.out.println("新增一个key：" + jedis.set("b", "s"));
		System.out.println("新增一个key：" + jedis.set("s", "aa"));
		System.out.println("查看当前key是否存在：" + jedis.exists("k"));
		System.out.println("查看当前key是否存在：" + jedis.exists("a"));
		System.out.println("查看当前数据库所有 key：" + jedis.keys("*"));
		System.out.println("设置当前key过期时间：" + jedis.expire("s", 5));
		try {
			Thread.sleep(2000);
			System.out.println("休眠 2S 后查看当前key剩余过期时间：" + jedis.ttl("s"));
		} finally {
			Thread.sleep(2000);
			System.out.println("再次休眠 2S 后查看当前key剩余过期时间：" + jedis.ttl("s"));
		}
		System.out.println("移除当前的key：" + jedis.move("b", 1));
		System.out.println("查看当前数据库所有 key：" + jedis.keys("*"));
		System.out.println("查看当前key的类型：" + jedis.type("k"));
		System.out.println("清空当前数据库：" + jedis.flushDB());
		jedis.disconnect();
		jedis.close();

	}
}
