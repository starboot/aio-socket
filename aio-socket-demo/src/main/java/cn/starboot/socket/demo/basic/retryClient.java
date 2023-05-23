package cn.starboot.socket.demo.basic;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.intf.AioHandler;

import java.io.IOException;

/**
 * 客户端如果设置一个线程 偶尔会出现死锁  即 read和connect之间的死锁
 */
public class retryClient {


	public static void main(String[] args) {
		TIMClient.getInstance().start();

//		con();
	}

	private static void con() {
		ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, new ClientHandler());

		// 配置类
		bootstrap.setBufferFactory(10 * 1024 * 1024, 10, true)
//					.addPlugin(new ReconnectPlugin(clientBootstrap))
//                    .addHeartPacket()
				.setWriteBufferSize(32 * 1024, 128)
				.setReadBufferSize(32 * 1024);

		try {
			ChannelContext start;
			do {
				start = bootstrap.start();
				Thread.sleep(1000L);
			} while (start == null);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}



	private static class TIMClient {

		private ChannelContext clientChannelContext;

		private final ClientBootstrap clientBootstrap;

		// 使用枚举构建单例模式
		private enum TIMClientStarterSingletonEnum {
			INSTANCE;
			private final TIMClient timServerStarter;
			TIMClientStarterSingletonEnum() {
				timServerStarter = new TIMClient(new ClientHandler());
			}
			private TIMClient getTimServerStarter() {
				return timServerStarter;
			}
		}

		// 对外部提供的获取单例的方法
		public static TIMClient getInstance() {
			return TIMClientStarterSingletonEnum.INSTANCE.getTimServerStarter();
		}

		public TIMClient(AioHandler aioHandler) {
			this.clientBootstrap = new ClientBootstrap("localhost", 8888, aioHandler);
		}


		public void start() {
			init();

			try {
				do {
					clientChannelContext = clientBootstrap.start();
					Thread.sleep(1000L);
				} while (clientChannelContext == null);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void init() {
			// 配置类
			clientBootstrap.setBufferFactory(10 * 1024 * 1024, 10, true)
//					.addPlugin(new ReconnectPlugin(clientBootstrap))
//                    .addHeartPacket()
					.setWriteBufferSize(32 * 1024, 128)
					.setReadBufferSize(32 * 1024);
		}
	}
}

