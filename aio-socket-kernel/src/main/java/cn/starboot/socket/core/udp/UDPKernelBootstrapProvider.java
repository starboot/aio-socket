package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.ClientBootstrap;
import cn.starboot.socket.core.ServerBootstrap;

public abstract class UDPKernelBootstrapProvider {

	private static Void checkPermission() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new RuntimePermission("asynchronousChannelProvider"));
		return null;
	}
	private UDPKernelBootstrapProvider(Void ignore) { }

	/**
	 * Initializes a new instance of this class.
	 *
	 * @throws  SecurityException
	 *          If a security manager has been installed and it denies
	 *          {@link RuntimePermission}{@code ("asynchronousChannelProvider")}
	 */
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

	public abstract ServerBootstrap openUDPServerBootstrap();

	public abstract ClientBootstrap openUDPClientBootstrap();
}
