package cn.starboot.socket.demo.reconnect;

import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.tcp.TCPClientBootstrap;
import cn.starboot.socket.core.plugins.ACKPlugin;
import cn.starboot.socket.core.plugins.ReconnectPlugin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HeartBeatClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		StringPacket demoPacket = new StringPacket("HELLO WORD");
		demoPacket.setReq(555);
		TCPClientBootstrap bootstrap = new TCPClientBootstrap("127.0.0.1", 8888, new ClientHandler());
		ChannelContext context = bootstrap
				// 心跳插件
				.addHeartPacket(new StringPacket("heartbeat message"))
				// 重连插件
				.addPlugin(new ReconnectPlugin(bootstrap))
				// ACK收到确认插件
				.addPlugin(new ACKPlugin(5, 3, TimeUnit.SECONDS))
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
