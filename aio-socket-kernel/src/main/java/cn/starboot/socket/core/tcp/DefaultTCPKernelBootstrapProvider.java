package cn.starboot.socket.core.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class DefaultTCPKernelBootstrapProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTCPKernelBootstrapProvider.class);

	private static final TCPKernelBootstrapProvider INSTANCE;

	static {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Load default TCP kernel bootstrap provider.");
		}
		PrivilegedAction<TCPKernelBootstrapProvider> pa = TCPKernelBootstrapProvider::new;
		INSTANCE = AccessController.doPrivileged(pa);
	}

	/**
	 * Prevent instantiation.
	 */
	private DefaultTCPKernelBootstrapProvider() { }

	/**
	 * Returns the default AsynchronousChannelProvider.
	 */
	public static TCPKernelBootstrapProvider create() {
		return INSTANCE;
	}
}
