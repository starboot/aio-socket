package cn.starboot.socket.core.jdk.aio.impl;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelProvider;

/**
 * Creates this platform's default AsynchronousChannelProvider
 *
 * @author MDong
 */
public class DefaultImproveAsynchronousChannelProvider {

	/**
	 * Prevent instantiation.
	 */
	private DefaultImproveAsynchronousChannelProvider() { }

	/**
	 * Returns the default AsynchronousChannelProvider.
	 */
	public static ImproveAsynchronousChannelProvider create() {
		return new ImproveAsynchronousChannelProviderImpl();
	}
}
