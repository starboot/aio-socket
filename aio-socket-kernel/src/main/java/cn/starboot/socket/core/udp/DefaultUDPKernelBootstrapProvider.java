package cn.starboot.socket.core.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class DefaultUDPKernelBootstrapProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUDPKernelBootstrapProvider.class);

	private static final UDPKernelBootstrapProvider INSTANCE;

	static {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Load default UDP kernel bootstrap provider.");
		}
		PrivilegedAction<UDPKernelBootstrapProvider> pa = UDPKernelBootstrapProvider::new;
		INSTANCE = AccessController.doPrivileged(pa);
	}

	/**
	 * Prevent instantiation.
	 */
	private DefaultUDPKernelBootstrapProvider() { }

	/**
	 * Returns the default AsynchronousChannelProvider.
	 */
	public static UDPKernelBootstrapProvider create() {
		return INSTANCE;
	}
}
