package cn.starboot.socket.demo.heartbeat;

import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.plugins.ACKPlugin;
import cn.starboot.socket.plugins.ReconnectPlugin;
import cn.starboot.socket.utils.pool.memory.MemoryPool;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HeartBeatClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		StringPacket demoPacket = new StringPacket("HELLO WORD");
		demoPacket.setReq("555");
		ClientBootstrap bootstrap = new ClientBootstrap("127.0.0.1", 8888, new ClientHandler());
		ChannelContext context = bootstrap
				// 心跳插件
				.addHeartPacket(new StringPacket("heartbeat message"))
				// 重连插件
				.addPlugin(new ReconnectPlugin(bootstrap))
				// ACK收到确认插件
				.addPlugin(new ACKPlugin(5, 3, TimeUnit.SECONDS, (packet, lastTime) -> System.out.println(packet.getReq() + " 超时了")))
				// 设置内存池工厂
				.setBufferFactory(1024 * 1024, 1, true)
				// 启动
				.start();
		Thread.sleep(1000);
		Aio.send(context, demoPacket);

		Thread.sleep(1000);

		bootstrap.shutdown();


	}
}
