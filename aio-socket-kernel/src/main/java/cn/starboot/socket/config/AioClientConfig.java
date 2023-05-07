package cn.starboot.socket.config;

import cn.starboot.socket.core.AioConfig;

public class AioClientConfig extends AioConfig {
	@Override
	public String getName() {
		return "client";
	}

	@Override
	public boolean isServer() {
		return false;
	}
}