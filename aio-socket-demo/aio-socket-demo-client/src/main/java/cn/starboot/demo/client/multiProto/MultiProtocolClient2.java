package cn.starboot.demo.client.multiProto;

import cn.starboot.demo.common.TestPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.utils.pool.memory.MemoryPool;

import java.io.IOException;

public class MultiProtocolClient2 {

	public static void main(String[] args) throws IOException, InterruptedException {

		TestPacket packet = new TestPacket("HELLO");
		ClientBootstrap bootstrap = new ClientBootstrap("127.0.0.1", 8888, new MyClientHandler());
		ChannelContext context = bootstrap

				.setBufferFactory(() -> new MemoryPool(1024 * 1024, 1, true))
				.start();
		Thread.sleep(1000);
		Aio.send(context, packet);

//		Thread.sleep(1000);

//		bootstrap.shutdown();


	}
}
