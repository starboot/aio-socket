package cn.starboot.demo.client.streamClient;

import cn.starboot.demo.common.TestPacket;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.utils.ThreadUtils;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.utils.pool.memory.MemoryPoolFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutorService;

public class StreamClient {

	public static void main(String[] args) throws IOException {

		TestPacket packet = new TestPacket("HELLO-AIO-SOCKET,HELLO-AIO-SOCKET");

		ExecutorService groupExecutor = ThreadUtils.getGroupExecutor(Runtime.getRuntime().availableProcessors());
		AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(groupExecutor);
		MemoryPoolFactory poolFactory = () -> new MemoryPool(10 * 1024 * 1024, 10, true);
		MyStreamClientHandler clientHandler = new MyStreamClientHandler();
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, clientHandler);
				bootstrap.setBufferFactory(poolFactory)
						.setReadBufferSize(1024 * 1024)
						.setWriteBufferSize(1024 * 1024, 512)
				;

				try {
					ChannelContext start = bootstrap.start(asynchronousChannelGroup);
					long num = 0;
					long startTime = System.currentTimeMillis();
					while (num++ < Integer.MAX_VALUE) {
						Aio.send(start, packet);
					}
					System.out.println("安全消息结束" + (System.currentTimeMillis() - startTime));
					Thread.sleep(2000);
					bootstrap.shutdown();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}

			}).start();
		}

	}
}
