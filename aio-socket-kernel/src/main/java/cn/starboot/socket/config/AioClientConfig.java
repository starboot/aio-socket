package cn.starboot.socket.config;

import cn.starboot.socket.core.AioConfig;

public class AioClientConfig extends AioConfig {

	/**
	 * 客户端线程数默认为2;就够用了
	 */
	private int bossThreadNumber = 1;

	@Override
	public String getName() {
		return "Client Configuration";
	}

	@Override
	public boolean isServer() {
		return false;
	}

	@Override
	public void setBossThreadNumber(int bossThreadNumber) {
		this.bossThreadNumber = bossThreadNumber;
	}

	@Override
	public int getBossThreadNumber() {
		return this.bossThreadNumber;
	}

}
