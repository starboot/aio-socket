package cn.starboot.socket.demo.heartbeat;

import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.plugins.ACKPlugin;
import cn.starboot.socket.core.plugins.ReconnectPlugin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HeartBeatClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		StringPacket demoPacket = new StringPacket("HELLO WORD");
		demoPacket.setReq(555);

		ClientBootstrap bootstrap = ClientBootstrap.startTCPService();

		bootstrap
				.remote("localhost", 8888)
				.addAioHandler(new ClientHandler())
				.setMemoryPoolFactory(1024 * 1024 * 4, 1, true)
				.setReadBufferSize(1024 * 1024)
				.setWriteBufferSize(1024 * 1024, 512)
				.setMemoryKeep(true)
				// 心跳插件
				.addHeartPacket(new StringPacket("heartbeat message"))
				// 重连插件
				.addPlugin(new ReconnectPlugin(bootstrap))
				// ACK收到确认插件
				.addPlugin(new ACKPlugin(5, 3, TimeUnit.SECONDS))
				;

		ChannelContext channelContext = bootstrap.start();

		Thread.sleep(1000);
		Aio.send(channelContext, demoPacket);

		Thread.sleep(1000);

		bootstrap.shutdown();


	}
}
