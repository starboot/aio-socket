/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.utils.pool.memory;

import cn.starboot.socket.utils.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ByteBuffer内存池
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class MemoryPool extends TimerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemoryPool.class);

	/**
	 * 守护线程定时清理内存页
	 *
	 * delay：首次启动时延
	 * period：时间间隔
	 * useInternal：是否启用专用守护线程，即不与其他组件或插件等共享线程资源
	 * threadName：线程名字
	 */
	private static final long delay = 500L;

	private static final long period = 1000L;

	private static final boolean useInternal = true;

	private static final String threadName = "MemoryPool Cleaner";

	/**
	 * 内存页游标
	 */
	private final AtomicInteger cursor = new AtomicInteger(0);

	/**
	 * 内存页组
	 */
	private MemoryBlock[] memoryBlocks;

	/**
	 * 定义内存页池可用状态
	 */
	private boolean enabled = true;

	/**
	 * 构造内存池
	 *
	 * @param memoryBlockSize 内存页大小
	 * @param memoryBlockNum  内存页个数
	 * @param isDirect        是否使用直接缓冲区
	 */
	public MemoryPool(final int memoryBlockSize, final int memoryBlockNum, final boolean isDirect) {
		super(delay, period, useInternal, threadName);
		memoryBlocks = new MemoryBlock[memoryBlockNum];
		for (int i = 0; i < memoryBlockNum; i++) {
			memoryBlocks[i] = new MemoryBlock(memoryBlockSize, isDirect);
		}
		if (memoryBlockNum == 0 || memoryBlockSize == 0) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("The Memory pool is not turned on");
			}
//			release();
			shutdown();
		}
	}

	/**
	 * 申请内存页
	 *
	 * @return 缓存页对象
	 */
	public MemoryBlock allocateBufferPage() {
		assertEnabled();
		//轮训游标，均衡分配内存页
		return memoryBlocks[(cursor.getAndIncrement() & Integer.MAX_VALUE) % memoryBlocks.length];
	}

	/**
	 * 检查内存池状态
	 */
	private void assertEnabled() {
		if (!enabled) {
			throw new IllegalStateException("buffer pool is disable");
		}
	}

	/**
	 * 释放回收内存
	 */
	public void release() {
		enabled = false;
	}

	/**
	 * 内存回收任务
	 */
	@Override
	public void run() {
		if (enabled) {
			for (MemoryBlock memoryBlock : memoryBlocks) {
				memoryBlock.tryClean();
			}
		} else {
			if (memoryBlocks != null) {
				for (MemoryBlock memoryBlock : memoryBlocks) {
					memoryBlock.release();
				}
				memoryBlocks = null;
			}
			shutdown();
		}
	}
}

