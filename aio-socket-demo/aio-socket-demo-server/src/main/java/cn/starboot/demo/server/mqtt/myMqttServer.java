package cn.starboot.demo.server.mqtt;

import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.utils.pool.memory.MemoryPool;

public class myMqttServer {

	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap("localhost", 8888, new myMqttHandler());
		bootstrap.setMemoryPoolFactory(() -> new MemoryPool(10 * 1024 * 1024, 10, true))
				.setReadBufferSize(1024 * 1024)
				.setWriteBufferSize(1024 * 4, 512)
				// 注册流量监控插件
//                .addPlugin(new StreamMonitorPlugin())
//				.addPlugin(new MonitorPlugin(5))
				.start();
	}
}
