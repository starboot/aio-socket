package cn.starboot.socket.core.spi;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import cn.starboot.socket.core.tcp.TCPKernelBootstrapProvider;
import cn.starboot.socket.core.udp.UDPKernelBootstrapProvider;

final class KernelBootstrapProviderImpl extends KernelBootstrapProvider {

	KernelBootstrapProviderImpl() {
	}

	@Override
	public ServerBootstrap openTCPServerBootstrap() {
		return TCPKernelBootstrapProvider.provider().openTCPServerBootstrap();
	}

	@Override
	public ServerBootstrap openUDPServerBootstrap() {
		return UDPKernelBootstrapProvider.provider().openUDPServerBootstrap();
	}

	@Override
	public ClientBootstrap openTCPClientBootstrap() {
		return TCPKernelBootstrapProvider.provider().openTCPClientBootstrap();
	}

	@Override
	public ClientBootstrap openUDPClientBootstrap() {
		return UDPKernelBootstrapProvider.provider().openUDPClientBootstrap();
	}
}
