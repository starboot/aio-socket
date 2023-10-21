package cn.starboot.socket.core;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.utils.ThreadUtils;
import cn.starboot.socket.core.utils.pool.memory.MemoryPool;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnitFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBootstrap implements Bootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBootstrap.class);

	/**
	 * 内存池
	 */
	protected MemoryPool memoryPool;

	/**
	 * 服务器配置类
	 */
	private final AioConfig config;

	/**
	 * 内核IO线程池
	 */
	protected ExecutorService bossExecutorService;


	/**
	 * 虚拟内存工厂，这里为读操作获取虚拟内存
	 */
	protected final MemoryUnitFactory readMemoryUnitFactory
			= memoryBlock -> memoryBlock.allocate(getConfig().getReadBufferSize());


	protected AbstractBootstrap(AioConfig config) {
		this.config = config;

	}


	/**
	 * 启动
	 */
	protected void beforeStart() throws IOException {
		getConfig().initMemoryPoolFactory();
		if (this.memoryPool == null) {
			this.memoryPool = getConfig().getMemoryPoolFactory().createMemoryPool();
		}

	}

	@Override
	public AioConfig getConfig() {
		return this.config;
	}

	/**
	 * 启动内核线程池
	 */
	protected void startExecutorService() {
		if (getConfig().getBossThreadNumber() > 0) {
			this.bossExecutorService = ThreadUtils.getGroupExecutor(getConfig().getBossThreadNumber());
		} else {
			this.bossExecutorService = ThreadUtils.getGroupExecutor();
		}
//        if (getConfig().getWorkerThreadNumber() > 0) {
//            this.workerExecutorService = ThreadUtils.getAioExecutor(getConfig().getWorkerThreadNumber());
//        }else {
//            this.workerExecutorService = ThreadUtils.getAioExecutor();
//        }
	}

	@Override
	public void shutdown() {
		if (this.memoryPool != null) {
			this.memoryPool.release();
		}
	}
}
