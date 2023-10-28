package cn.starboot.socket.core.tcp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

final class TCPKernelBootstrapProviderImpl extends TCPKernelBootstrapProvider {

	TCPKernelBootstrapProviderImpl() {
	}

	@Override
	public ServerBootstrap openTCPServerBootstrap(KernelBootstrapProvider kernelBootstrapProvider) {
		return new TCPServerBootstrap(this, kernelBootstrapProvider);
	}

	@Override
	public ClientBootstrap openTCPClientBootstrap(KernelBootstrapProvider kernelBootstrapProvider) {
		return new TCPClientBootstrap(this, kernelBootstrapProvider);
	}
}
