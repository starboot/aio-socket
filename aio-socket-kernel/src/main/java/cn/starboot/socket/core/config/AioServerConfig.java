package cn.starboot.socket.core.config;

import cn.starboot.socket.core.AioConfig;

public class AioServerConfig extends AioConfig {
	@Override
	public String getName() {
		return "aio-socket ServerBootstrap";
	}

	@Override
	public boolean isServer() {
		return true;
	}
}
