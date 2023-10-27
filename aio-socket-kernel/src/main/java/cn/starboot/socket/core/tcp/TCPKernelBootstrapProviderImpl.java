package cn.starboot.socket.core.tcp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;

final class TCPKernelBootstrapProviderImpl extends TCPKernelBootstrapProvider {

	TCPKernelBootstrapProviderImpl() {
	}

	@Override
	public ServerBootstrap openTCPServerBootstrap() {
		return new TCPServerBootstrap(this);
	}

	@Override
	public ClientBootstrap openTCPClientBootstrap() {
		return new TCPClientBootstrap(this);
	}
}
