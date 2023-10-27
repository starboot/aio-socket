package cn.starboot.socket.demo.mutiproto;

import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.tcp.TCPClientBootstrap;

import java.io.IOException;

public class MultiProtocolClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		StringPacket packet = new StringPacket("HELLO WORD");
		TCPClientBootstrap bootstrap = new TCPClientBootstrap("127.0.0.1", 8888, new ClientHandler());
		ChannelContext context = bootstrap

				.setBufferFactory(1024 * 1024, 1, true)
				.start();
		Thread.sleep(1000);
		Aio.send(context, packet);

//		Thread.sleep(1000);

//		bootstrap.shutdown();


	}
}
