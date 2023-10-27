package cn.starboot.socket.core;

import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.plugins.Plugin;

public interface Bootstrap<T> {

	void shutdown();

	AioConfig getConfig();

	T setThreadNum(int bossThreadNum);

	T setMemoryPoolFactory(int size, int num, boolean useDirect);

	T setWriteBufferSize(int writeBufferSize, int maxWaitNum);

	T setReadBufferSize(int readBufferSize);

	T setMemoryKeep(boolean isMemoryKeep);

	T addPlugin(Plugin plugin);

	T addAioHandler(AioHandler handler);
}
