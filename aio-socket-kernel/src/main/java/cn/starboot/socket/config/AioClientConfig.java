package cn.starboot.socket.config;

import cn.starboot.socket.core.AioConfig;

public class AioClientConfig extends AioConfig {

	/**
	 * 客户端线程数默认为2;就够用了
	 */
	private int bossThreadNumber = 2;

	@Override
	public String getName() {
		return "aio-socket: AioClientConfig";
	}

	@Override
	public boolean isServer() {
		return false;
	}

	@Override
	public AioConfig setBossThreadNumber(int bossThreadNumber) {
		this.bossThreadNumber = bossThreadNumber;
		return this;
	}

	@Override
	public AioConfig setWorkerThreadNumber(int workerThreadNumber) {
		return this;
	}

	@Override
	public int getBossThreadNumber() {
		return this.bossThreadNumber;
	}

	@Override
	public int getWorkerThreadNumber() {
		return 0;
	}
}
