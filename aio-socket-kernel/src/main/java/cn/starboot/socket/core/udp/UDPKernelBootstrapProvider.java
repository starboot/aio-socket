package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;

public final class UDPKernelBootstrapProvider {

	UDPKernelBootstrapProvider() {
	}

	public ServerBootstrap openUDPServerBootstrap() {
		return new UDPServerBootstrap(this);
	}

	public ClientBootstrap openUDPClientBootstrap() {
		return new UDPClientBootstrap(this);
	}
}
