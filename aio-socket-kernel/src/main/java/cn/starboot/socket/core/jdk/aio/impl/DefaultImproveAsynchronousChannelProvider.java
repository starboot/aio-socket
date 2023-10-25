package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelProvider;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Creates this platform's default AsynchronousChannelProvider
 *
 * @author MDong
 */
public final class DefaultImproveAsynchronousChannelProvider {

	private static final ImproveAsynchronousChannelProvider INSTANCE;

	static {
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
