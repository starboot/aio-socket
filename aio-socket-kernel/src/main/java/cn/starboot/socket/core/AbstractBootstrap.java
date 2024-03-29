package cn.starboot.socket.core;

import cn.starboot.socket.core.banner.AioSocketBanner;
import cn.starboot.socket.core.functional.MemoryUnitSupplier;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import cn.starboot.socket.core.utils.ThreadUtils;
import cn.starboot.socket.core.utils.pool.memory.MemoryPool;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnitFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public abstract class AbstractBootstrap {

	/**
	 * 内存池
	 */
	private MemoryPool memoryPool;

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
	private final MemoryUnitFactory readMemoryUnitFactory
			= memoryBlock -> memoryBlock.allocate(getConfig().getReadBufferSize());

	private final MemoryUnitFactory writeMemoryUnitFactory
			= memoryBlock -> memoryBlock.allocate(getConfig().getWriteBufferSize());

	private final MemoryUnitSupplier readMemoryUnitSupplier = new MemoryUnitSupplier() {
		@Override
		public MemoryUnit applyMemoryUnit() {
			return readMemoryUnitFactory.createMemoryUnit(memoryPool.allocateMemoryBlock());
		}
	};

	private final MemoryUnitSupplier writeMemoryUnitSupplier = new MemoryUnitSupplier() {
		@Override
		public MemoryUnit applyMemoryUnit() {
			return writeMemoryUnitFactory.createMemoryUnit(memoryPool.allocateMemoryBlock());
		}
	};

	public abstract KernelBootstrapProvider KernelProvider();

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

	protected MemoryUnitSupplier getReadMemoryUnitSupplier() {
		return readMemoryUnitSupplier;
	}

	protected MemoryUnitSupplier getWriteMemoryUnitSupplier() {
		return writeMemoryUnitSupplier;
	}

	public AioConfig getConfig() {
		return this.config;
	}

	/**
	 * 启动内核线程池
	 */
	protected void startExecutorService() {
		if (getConfig().getKernelThreadNumber() > 0) {
			this.bossExecutorService = ThreadUtils.getGroupExecutor(getConfig().getKernelThreadNumber());
		} else {
			this.bossExecutorService = ThreadUtils.getGroupExecutor();
		}
//        if (getConfig().getWorkerThreadNumber() > 0) {
//            this.workerExecutorService = ThreadUtils.getAioExecutor(getConfig().getWorkerThreadNumber());
//        }else {
//            this.workerExecutorService = ThreadUtils.getAioExecutor();
//        }
	}

//	@Override
	public void shutdown() {
		if (this.memoryPool != null) {
			this.memoryPool.release();
		}
	}


}
