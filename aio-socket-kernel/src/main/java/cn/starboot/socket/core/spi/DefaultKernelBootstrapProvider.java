package cn.starboot.socket.core.spi;

import cn.starboot.socket.core.banner.AioSocketBanner;
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
		new AioSocketBanner().printBanner(System.out);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Load default kernel bootstrap provider.");
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
