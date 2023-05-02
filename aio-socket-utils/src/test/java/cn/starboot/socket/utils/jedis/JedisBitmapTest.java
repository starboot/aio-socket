package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.Jedis;

/**
 * 举例：  user为用户   1，2，3，4，5，6，7 代表周一到周末  false-未打卡  true-打卡
 *   周一  :  false
 *   周二  :  false
 *   周三  :  false
 *   周四  :  false
 *   周五  :  false
 *   周六  :  false
 *   周日  :  false
 * 查看某一天是否有打卡
 *   查看周三  :  true
 *   查看周六  :  false
 * 统计打卡的天数
 * 5
 *
 * bitmaps 位图，数据结构；都是操作二进制来进行记录，只有0和1两个状态；
 */
public class JedisBitmapTest {
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(11);
		jedis.flushDB();
		System.out.println("举例：  user为用户   1，2，3，4，5，6，7 代表周一到周末  false-未打卡  true-打卡");
		System.out.println("  周一  :  "+jedis.setbit("user", 1, true));
		System.out.println("  周二  :  "+jedis.setbit("user", 2, true));
		System.out.println("  周三  :  "+jedis.setbit("user", 3, true));
		System.out.println("  周四  :  "+jedis.setbit("user", 4, true));
		System.out.println("  周五  :  "+jedis.setbit("user", 5, true));
		System.out.println("  周六  :  "+jedis.setbit("user", 6, false));
		System.out.println("  周日  :  "+jedis.setbit("user", 7, false));
		System.out.println("查看某一天是否有打卡");
		System.out.println("  查看周三  :  "+jedis.getbit("user", 3));
		System.out.println("  查看周六  :  "+jedis.getbit("user", 6));
		System.out.println("统计打卡的天数");
		System.out.println(+jedis.bitcount("user"));
		jedis.flushDB();
	}
}
