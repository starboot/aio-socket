package cn.starboot.socket.core.config;

public class AioClientConfig extends AioConfig{
	@Override
	public String getName() {
		return "client";
	}

	@Override
	public boolean isServer() {
		return false;
	}
}
