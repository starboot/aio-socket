package cn.starboot.socket.utils.jedis;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.params.GeoRadiusParam;

import java.util.HashMap;
import java.util.Map;

/**
 *   添加单个地理位置的坐标  :  1
 *   添加多个地理位置的坐标  :  4
 *   获取单个地理位置的坐标  :  [(109.9410679936409,33.862730262164945)]
 *   获取多个地理位置的坐标  :  [(108.9342525601387,34.230530975990824), (107.03193873167038,33.067839171875086)]
 *   计算两个位置之间的距离  :  218504.7803
 *   计算两个位置之间的距离  :  264050.1287
 * -----------------------------------------------
 *   以108.01 33.01为坐标 查询直线距离300km的城市  :  [redis.clients.jedis.GeoRadiusResponse@306a30c7, redis.clients.jedis.GeoRadiusResponse@b81eda8, redis.clients.jedis.GeoRadiusResponse@68de145, redis.clients.jedis.GeoRadiusResponse@27fa135a, redis.clients.jedis.GeoRadiusResponse@46f7f36a]
 *   以108.01 33.01为坐标 查询直线距离150km的城市  :  [redis.clients.jedis.GeoRadiusResponse@421faab1, redis.clients.jedis.GeoRadiusResponse@2b71fc7e]
 *   查询直线距离300km的城市，返回结果添加经度纬度和直线距离  :  [redis.clients.jedis.GeoRadiusResponse@1a86f2f1, redis.clients.jedis.GeoRadiusResponse@3eb07fd3, redis.clients.jedis.GeoRadiusResponse@506c589e, redis.clients.jedis.GeoRadiusResponse@69d0a921, redis.clients.jedis.GeoRadiusResponse@446cdf90]
 *   查询直线距离300km的城市，返回结果添加经度纬度和直线距离，结果只匹配二个元素  :  [redis.clients.jedis.GeoRadiusResponse@799f7e29, redis.clients.jedis.GeoRadiusResponse@4b85612c]
 *   找出距离指定元素的指定距离内的其他元素  :  [redis.clients.jedis.GeoRadiusResponse@277050dc, redis.clients.jedis.GeoRadiusResponse@5c29bfd, redis.clients.jedis.GeoRadiusResponse@7aec35a, redis.clients.jedis.GeoRadiusResponse@67424e82]
 *   找出距离指定元素的指定距离内的其他元素  :  [redis.clients.jedis.GeoRadiusResponse@42110406, redis.clients.jedis.GeoRadiusResponse@531d72ca]
 *   geohash 返回一个或多个位置元素的 [Geohash] 表示  :  [wqj6wz7x210, wmuh5ef60u0]
 * -----------------------------------------------
 *   获取全部元素  :  [hanzhong, ankang, xian, shangluo, tongchuan]
 *   删除指定元素  :  1
 *   获取全部元素  :  [hanzhong, ankang, xian, shangluo]
 *
 * Process finished with exit code 0
 */
public class JedisGeospatialTest {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost", 6379);
		jedis.select(9);
		jedis.flushDB();
		System.out.println("  添加单个地理位置的坐标  :  "+jedis.geoadd("city", 108.93425, 34.23053, "xian"));
		Map<String, GeoCoordinate> map = new HashMap<String, GeoCoordinate>();
		map.put("shangluo", new GeoCoordinate(109.94107, 33.86273));
		map.put("ankang", new GeoCoordinate(109.02697, 32.6955));
		map.put("hanzhong", new GeoCoordinate(107.03194, 33.06784));
		map.put("tongchuan", new GeoCoordinate(109.07593, 35.06914));
		System.out.println("  添加多个地理位置的坐标  :  "+jedis.geoadd("city", map));
		System.out.println("  获取单个地理位置的坐标  :  "+jedis.geopos("city","shangluo"));
		System.out.println("  获取多个地理位置的坐标  :  "+jedis.geopos("city","xian", "hanzhong"));
		System.out.println("  计算两个位置之间的距离  :  "+jedis.geodist("city", "xian", "hanzhong"));
		System.out.println("  计算两个位置之间的距离  :  "+jedis.geodist("city", "ankang", "tongchuan"));
		System.out.println("-----------------------------------------------");
		System.out.println("  以108.01 33.01为坐标 查询直线距离300km的城市  :  "+jedis.georadius("city", 108.01, 33.01, 300, GeoUnit.KM));
		System.out.println("  以108.01 33.01为坐标 查询直线距离150km的城市  :  "+jedis.georadius("city", 108.01, 33.01, 150, GeoUnit.KM));
		System.out.println("  查询直线距离300km的城市，返回结果添加经度纬度和直线距离  :  "+jedis.georadius("city", 108.01, 33.01, 300, GeoUnit.KM,
				GeoRadiusParam.geoRadiusParam().withDist().withCoord()));
		System.out.println("  查询直线距离300km的城市，返回结果添加经度纬度和直线距离，结果只匹配二个元素  :  "+jedis.georadius("city", 108.01, 33.01, 300, GeoUnit.KM,
				GeoRadiusParam.geoRadiusParam().withDist().withCoord().count(2)));
		System.out.println("  找出距离指定元素的指定距离内的其他元素  :  "+jedis.georadiusByMember("city", "xian", 200, GeoUnit.KM));
		System.out.println("  找出距离指定元素的指定距离内的其他元素  :  "+jedis.georadiusByMember("city", "hanzhong", 200, GeoUnit.KM));
		System.out.println("  geohash 返回一个或多个位置元素的 [Geohash] 表示  :  "+jedis.geohash("city", "xian", "hanzhong"));
		System.out.println("-----------------------------------------------");
		System.out.println("  获取全部元素  :  "+jedis.zrange("city", 0, -1));
		System.out.println("  删除指定元素  :  "+jedis.zrem("city", "tongchuan"));
		System.out.println("  获取全部元素  :  "+jedis.zrange("city", 0, -1));
		jedis.flushDB();


	}
}
