package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.DatagramBootstrap;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

final class UDPKernelBootstrapProviderImpl extends UDPKernelBootstrapProvider {

	UDPKernelBootstrapProviderImpl() {
	}

	@Override
	public DatagramBootstrap openUDPBootstrap(KernelBootstrapProvider kernelBootstrapProvider) {
		return new UDPBootstrap(this, kernelBootstrapProvider);
	}
}
