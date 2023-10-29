package cn.starboot.socket.core;

import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.plugins.Plugin;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

public interface Bootstrap<T> {

	void shutdown();

	default void shutdownNow() {

	}

	AioConfig getConfig();

	T setThreadNum(int threadNum);

	T setMemoryPoolFactory(int size, int num, boolean useDirect);

	T setWriteBufferSize(int writeBufferSize, int maxWaitNum);

	T setReadBufferSize(int readBufferSize);

	T setMemoryKeep(boolean isMemoryKeep);

	T addPlugin(Plugin plugin);

	T addAioHandler(AioHandler handler);
}
