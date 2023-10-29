package cn.starboot.socket.core.spi;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.DatagramBootstrap;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.tcp.TCPKernelBootstrapProvider;
import cn.starboot.socket.core.udp.UDPKernelBootstrapProvider;

final class KernelBootstrapProviderImpl extends KernelBootstrapProvider {

	KernelBootstrapProviderImpl() {
	}

	@Override
	public ServerBootstrap openTCPServerBootstrap() {
		return TCPKernelBootstrapProvider.provider().openTCPServerBootstrap(this);
	}

	@Override
	public DatagramBootstrap openUDPBootstrap() {
		return UDPKernelBootstrapProvider.provider().openUDPBootstrap(this);
	}

	@Override
	public ClientBootstrap openTCPClientBootstrap() {
		return TCPKernelBootstrapProvider.provider().openTCPClientBootstrap(this);
	}
}
