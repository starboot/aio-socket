package cn.starboot.socket.factory;

import cn.starboot.socket.core.ReadWriteBuff;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.utils.pool.memory.MemoryUnitFactory;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ReadWriteBuffWithMemoryUnitFactory<T, R> {

	R convert(T t);

	default Function<ReadWriteBuff, Supplier<MemoryUnit>> createFunction(MemoryUnitFactory memoryUnitFactory, MemoryPool memoryPool) {
		return readWriteBuff -> () -> {
			readWriteBuff.setReadBuffer(memoryUnitFactory.createBuffer(memoryPool.allocateBufferPage()));
			return readWriteBuff.getReadBuffer();
		};
	}
}
