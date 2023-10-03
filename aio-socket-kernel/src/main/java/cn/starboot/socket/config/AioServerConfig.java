package cn.starboot.socket.config;

import cn.starboot.socket.core.AioConfig;

public class AioServerConfig extends AioConfig {

	/**
	 * 内核IO线程池线程数量
	 */
	private int bossThreadNum;


	@Override
	public String getName() {
		return "Server Configuration";
	}

	@Override
	public boolean isServer() {
		return true;
	}

	@Override
	public void setBossThreadNumber(int bossThreadNumber) {
		this.bossThreadNum = bossThreadNumber;
	}

	@Override
	public int getBossThreadNumber() {
		return this.bossThreadNum;
	}

}
