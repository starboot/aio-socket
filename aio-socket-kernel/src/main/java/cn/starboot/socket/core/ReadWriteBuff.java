package cn.starboot.socket.core;

import cn.starboot.socket.utils.pool.memory.MemoryUnit;

public class ReadWriteBuff {

	/**
	 * 存放刚读到的数据
	 */
	private MemoryUnit readBuffer;

	/**
	 * 存放待发送的完整比特流
	 */
	private MemoryUnit writeBuffer;

	public MemoryUnit getReadBuffer() {
		return readBuffer;
	}

	public void setReadBuffer(MemoryUnit readBuffer) {
		this.readBuffer = readBuffer;
	}

	public MemoryUnit getWriteBuffer() {
		return writeBuffer;
	}

	public void setWriteBuffer(MemoryUnit writeBuffer) {
		this.writeBuffer = writeBuffer;
	}
}
