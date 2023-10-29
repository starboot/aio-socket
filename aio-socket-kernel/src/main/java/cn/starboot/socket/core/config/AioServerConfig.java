package cn.starboot.socket.core.config;

import cn.starboot.socket.core.AioConfig;

public class AioServerConfig extends AioConfig {

	/**
	 * 内核IO线程池线程数量
	 */
	private int kernelThreadNum = Runtime.getRuntime().availableProcessors();


	@Override
	public String getName() {
		return "Server Configuration";
	}

	@Override
	public boolean isServer() {
		return true;
	}

	@Override
	public void setKernelThreadNumber(int threadNum) {
		this.kernelThreadNum = threadNum;
	}

	@Override
	public int getKernelThreadNumber() {
		return this.kernelThreadNum;
	}

}
