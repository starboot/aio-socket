package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;

final class UDPKernelBootstrapProviderImpl extends UDPKernelBootstrapProvider {

	UDPKernelBootstrapProviderImpl() {
	}

	@Override
	public ServerBootstrap openUDPServerBootstrap() {
		return new UDPServerBootstrap(this);
	}

	@Override
	public ClientBootstrap openUDPClientBootstrap() {
		return new UDPClientBootstrap(this);
	}
}
