package cn.starboot.socket.config;

import cn.starboot.socket.core.AioConfig;

public class AioServerConfig extends AioConfig {

	/**
	 * 内核IO线程池线程数量
	 */
	private int bossThreadNum;

	/**
	 * 作业任务线程池线程数量
	 */
	private int workerThreadNum;

	@Override
	public String getName() {
		return "aio-socket: AioServerConfig";
	}

	@Override
	public boolean isServer() {
		return true;
	}

	@Override
	public AioConfig setBossThreadNumber(int bossThreadNumber) {
		this.bossThreadNum = bossThreadNumber;
		return this;
	}

	@Override
	public void setWorkerThreadNumber(int workerThreadNumber) {
		this.workerThreadNum = workerThreadNumber;
	}

	@Override
	public int getBossThreadNumber() {
		return this.bossThreadNum;
	}

	@Override
	public int getWorkerThreadNumber() {
		return this.workerThreadNum;
	}
}
