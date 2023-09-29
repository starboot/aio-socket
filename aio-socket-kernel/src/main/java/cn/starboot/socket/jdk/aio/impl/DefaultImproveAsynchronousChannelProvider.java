package cn.starboot.socket.jdk.aio.impl;

import cn.starboot.socket.jdk.aio.ImproveAsynchronousChannelProvider;

/**
 * Creates this platform's default AsynchronousChannelProvider
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
