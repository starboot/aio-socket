package cn.starboot.socket.core.config;

import cn.starboot.socket.core.AioConfig;

public class DatagramConfig extends AioConfig {

	/**
	 * 内核IO线程池线程数量
	 */
	private int kernelThreadNum = 2;

	@Override
	public String getName() {
		return "UDP Configuration";
	}

	@Override
	public boolean isServer() {
		return false;
	}

	@Override
	public void setKernelThreadNumber(int threadNum) {
		this.kernelThreadNum = threadNum;
	}

	@Override
	public int getKernelThreadNumber() {
		return kernelThreadNum;
	}
}
