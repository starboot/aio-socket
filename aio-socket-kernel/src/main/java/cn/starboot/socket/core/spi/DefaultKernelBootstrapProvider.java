package cn.starboot.socket.core.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Creates this platform's default DefaultKernelBootstrapProvider
 *
 * @author MDong
 */
final class DefaultKernelBootstrapProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKernelBootstrapProvider.class);

	private static final KernelBootstrapProviderImpl INSTANCE;

	static {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Load default improve asynchronous channel provider.");
		}
		PrivilegedAction<KernelBootstrapProviderImpl> pa = KernelBootstrapProviderImpl::new;
		INSTANCE = AccessController.doPrivileged(pa);
	}

	/**
	 * Prevent instantiation.
	 */
	private DefaultKernelBootstrapProvider() { }

	/**
	 * Returns the default AsynchronousChannelProvider.
	 */
	static KernelBootstrapProviderImpl create() {
		return INSTANCE;
	}

}
