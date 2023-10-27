package cn.starboot.socket.core.tcp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;

public final class TCPKernelBootstrapProvider {

	TCPKernelBootstrapProvider() {
	}

	public ServerBootstrap openTCPServerBootstrap() {
		return new TCPServerBootstrap(this);
	}

	public ClientBootstrap openTCPClientBootstrap() {
		return new TCPClientBootstrap(this);
	}
}
