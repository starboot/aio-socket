package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

final class UDPKernelBootstrapProviderImpl extends UDPKernelBootstrapProvider {

	UDPKernelBootstrapProviderImpl() {
	}

	@Override
	public ServerBootstrap openUDPServerBootstrap(KernelBootstrapProvider kernelBootstrapProvider) {
		return new UDPServerBootstrap(this, kernelBootstrapProvider);
	}

	@Override
	public ClientBootstrap openUDPClientBootstrap(KernelBootstrapProvider kernelBootstrapProvider) {
		return new UDPClientBootstrap(this, kernelBootstrapProvider);
	}
}
