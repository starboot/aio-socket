package cn.starboot.socket.core.spi.impl;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;
import cn.starboot.socket.core.tcp.DefaultTCPKernelBootstrapProvider;
import cn.starboot.socket.core.udp.DefaultUDPKernelBootstrapProvider;

final class KernelBootstrapProviderImpl extends KernelBootstrapProvider {

	KernelBootstrapProviderImpl() {
	}

	@Override
	public ServerBootstrap openTCPServerBootstrap() {
		return DefaultTCPKernelBootstrapProvider.create().openTCPServerBootstrap();
	}

	@Override
	public ServerBootstrap openUDPServerBootstrap() {
		return DefaultUDPKernelBootstrapProvider.create().openUDPServerBootstrap();
	}

	@Override
	public ClientBootstrap openTCPClientBootstrap() {
		return DefaultTCPKernelBootstrapProvider.create().openTCPClientBootstrap();
	}

	@Override
	public ClientBootstrap openUDPClientBootstrap() {
		return DefaultUDPKernelBootstrapProvider.create().openUDPClientBootstrap();
	}
}
