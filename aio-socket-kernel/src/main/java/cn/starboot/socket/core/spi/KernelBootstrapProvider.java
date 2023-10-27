package cn.starboot.socket.core.spi;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;

public abstract class KernelBootstrapProvider {

	private static Void checkPermission() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new RuntimePermission("asynchronousChannelProvider"));
		return null;
	}
	private KernelBootstrapProvider(Void ignore) { }

	/**
	 * Initializes a new instance of this class.
	 *
	 * @throws  SecurityException
	 *          If a security manager has been installed and it denies
	 *          {@link RuntimePermission}{@code ("asynchronousChannelProvider")}
	 */
	protected KernelBootstrapProvider() {
		this(checkPermission());
	}

	// lazy initialization of default provider
	private static class ProviderHolder {
		static final KernelBootstrapProvider provider = load();

		private static KernelBootstrapProvider load() {
			return cn.starboot.socket.core.spi.impl.DefaultKernelBootstrapProvider.create();
		}

	}

	public static KernelBootstrapProvider provider() {
		return KernelBootstrapProvider.ProviderHolder.provider;
	}

	public abstract ServerBootstrap openTCPServerBootstrap();

	public abstract ServerBootstrap openUDPServerBootstrap();

	public abstract ClientBootstrap openTCPClientBootstrap();

	public abstract ClientBootstrap openUDPClientBootstrap();

}
