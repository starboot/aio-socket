package cn.starboot.socket.demo.mutiproto;

import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.tcp.ClientBootstrap;
import cn.starboot.socket.demo.DemoPacket;

import java.io.IOException;

public class MultiProtocolClient2 {

	public static void main(String[] args) throws IOException, InterruptedException {

		DemoPacket packet = new DemoPacket("HELLO");
		ClientBootstrap bootstrap = new ClientBootstrap("127.0.0.1", 8888, new MyClientHandler());
		ChannelContext context = bootstrap

				.setBufferFactory(1024 * 1024, 1, true)
				.start();
		Thread.sleep(1000);
		Aio.send(context, packet);

//		Thread.sleep(1000);

//		bootstrap.shutdown();


	}
}
