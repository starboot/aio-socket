package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

public abstract class UDPKernelBootstrapProvider {

	private static Void checkPermission() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new RuntimePermission("asynchronousChannelProvider"));
		return null;
	}
	private UDPKernelBootstrapProvider(Void ignore) { }

	protected UDPKernelBootstrapProvider() {
		this(checkPermission());
	}

	// lazy initialization of default provider
	private static class ProviderHolder {
		static final UDPKernelBootstrapProvider provider = load();

		private static UDPKernelBootstrapProvider load() {
			return cn.starboot.socket.core.udp.DefaultUDPKernelBootstrapProvider.create();
		}

	}

	public static UDPKernelBootstrapProvider provider() {
		return UDPKernelBootstrapProvider.ProviderHolder.provider;
	}

	public abstract ServerBootstrap openUDPServerBootstrap(KernelBootstrapProvider kernelBootstrapProvider);

	public abstract ClientBootstrap openUDPClientBootstrap(KernelBootstrapProvider kernelBootstrapProvider);
}
