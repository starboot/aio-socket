package cn.starboot.demo.client.multiProto;

import cn.starboot.demo.client.ClientHandler;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.utils.pool.memory.MemoryPool;

import java.io.IOException;

public class MultiProtocolClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		StringPacket packet = new StringPacket("HELLO WORD");
		ClientBootstrap bootstrap = new ClientBootstrap("127.0.0.1", 8888, new ClientHandler());
		ChannelContext context = bootstrap

				.setBufferFactory(() -> new MemoryPool(1024 * 1024, 10, true))
				.start();
		Thread.sleep(1000);
		Aio.send(context, packet);

//		Thread.sleep(1000);

//		bootstrap.shutdown();


	}
}