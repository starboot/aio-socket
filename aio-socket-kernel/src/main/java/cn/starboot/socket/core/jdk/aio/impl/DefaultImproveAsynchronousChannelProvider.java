package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Creates this platform's default AsynchronousChannelProvider
 *
 * @author MDong
 */
public final class DefaultImproveAsynchronousChannelProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultImproveAsynchronousChannelProvider.class);

	private static final ImproveAsynchronousChannelProvider INSTANCE;

	static {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Load default improve asynchronous channel provider.");
		}
		PrivilegedAction<ImproveAsynchronousChannelProvider> pa = ImproveAsynchronousChannelProviderImpl::new;
		INSTANCE = AccessController.doPrivileged(pa);
	}

	/**
	 * Prevent instantiation.
	 */
	private DefaultImproveAsynchronousChannelProvider() { }

	/**
	 * Returns the default AsynchronousChannelProvider.
	 */
	public static ImproveAsynchronousChannelProvider create() {
		return INSTANCE;
	}
}
