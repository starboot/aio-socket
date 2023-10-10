package cn.starboot.socket.core;

public interface Bootstrap {

	AioConfig getConfig();

	void shutdown();
}
