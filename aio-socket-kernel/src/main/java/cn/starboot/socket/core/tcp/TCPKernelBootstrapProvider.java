package cn.starboot.socket.core.tcp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;
import cn.starboot.socket.core.spi.KernelBootstrapProvider;

public abstract class TCPKernelBootstrapProvider {

	private static Void checkPermission() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new RuntimePermission("asynchronousChannelProvider"));
		return null;
	}
	private TCPKernelBootstrapProvider(Void ignore) { }

	/**
	 * Initializes a new instance of this class.
	 *
	 * @throws  SecurityException
	 *          If a security manager has been installed and it denies
	 *          {@link RuntimePermission}{@code ("asynchronousChannelProvider")}
	 */
	protected TCPKernelBootstrapProvider() {
		this(checkPermission());
	}

	// lazy initialization of default provider
	private static class ProviderHolder {
		static final TCPKernelBootstrapProvider provider = load();

		private static TCPKernelBootstrapProvider load() {
			return cn.starboot.socket.core.tcp.DefaultTCPKernelBootstrapProvider.create();
		}

	}

	public static TCPKernelBootstrapProvider provider() {
		return TCPKernelBootstrapProvider.ProviderHolder.provider;
	}

	public abstract ServerBootstrap openTCPServerBootstrap(KernelBootstrapProvider kernelBootstrapProvider);

	public abstract ClientBootstrap openTCPClientBootstrap(KernelBootstrapProvider kernelBootstrapProvider);
}
