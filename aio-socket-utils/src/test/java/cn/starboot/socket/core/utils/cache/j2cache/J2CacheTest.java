package cn.starboot.socket.core.utils.cache.j2cache;

import cn.starboot.socket.core.utils.cache.TestObj;
import net.oschina.j2cache.CacheChannel;

public class J2CacheTest {

	public static void main(String[] args) {

		J2Cache j2 = J2Cache.register("J2");

		j2.put("testKey", new TestObj("mxd", "男", "哈哈哈哈"));

		j2.put("testKey11", new TestObj("mxd", "男", "哈哈哈哈111"));

		System.out.println(j2.keys());

		System.out.println(j2.get("testKey"));

		System.out.println(j2.get("testKey", TestObj.class));

		j2.remove("testKey");

		System.out.println(j2.keys());

		CacheChannel channel = net.oschina.j2cache.J2Cache.getChannel();


	}
}
