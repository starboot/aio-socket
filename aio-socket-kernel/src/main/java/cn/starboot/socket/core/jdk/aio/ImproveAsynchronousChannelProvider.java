package cn.starboot.socket.core.jdk.aio;

import java.io.IOException;
import java.nio.channels.IllegalChannelGroupException;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public abstract class ImproveAsynchronousChannelProvider {

	private static Void checkPermission() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null)
			sm.checkPermission(new RuntimePermission("asynchronousChannelProvider"));
		return null;
	}
	private ImproveAsynchronousChannelProvider(Void ignore) { }

	/**
	 * Initializes a new instance of this class.
	 *
	 * @throws  SecurityException
	 *          If a security manager has been installed and it denies
	 *          {@link RuntimePermission}{@code ("asynchronousChannelProvider")}
	 */
	protected ImproveAsynchronousChannelProvider() {
		this(checkPermission());
	}

	// lazy initialization of default provider
	private static class ProviderHolder {
		static final ImproveAsynchronousChannelProvider provider = load();

		private static ImproveAsynchronousChannelProvider load() {
			return cn.starboot.socket.core.jdk.aio.impl.DefaultImproveAsynchronousChannelProvider.create();
		}

	}

	/**
	 * Returns the system-wide default asynchronous channel provider for this
	 * invocation of the Java virtual machine.
	 *
	 * <p> The first invocation of this method locates the default provider
	 * object as follows: </p>
	 *
	 * <ol>
	 *
	 *   <li><p> If the system property
	 *   {@code java.nio.channels.spi.AsynchronousChannelProvider} is defined
	 *   then it is taken to be the fully-qualified name of a concrete provider class.
	 *   The class is loaded and instantiated; if this process fails then an
	 *   unspecified error is thrown.  </p></li>
	 *
	 *   <li><p> If a provider class has been installed in a jar file that is
	 *   visible to the system class loader, and that jar file contains a
	 *   provider-configuration file named
	 *   {@code java.nio.channels.spi.AsynchronousChannelProvider} in the resource
	 *   directory {@code META-INF/services}, then the first class name
	 *   specified in that file is taken.  The class is loaded and
	 *   instantiated; if this process fails then an unspecified error is
	 *   thrown.  </p></li>
	 *
	 *   <li><p> Finally, if no provider has been specified by any of the above
	 *   means then the system-default provider class is instantiated and the
	 *   result is returned.  </p></li>
	 *
	 * </ol>
	 *
	 * <p> Subsequent invocations of this method return the provider that was
	 * returned by the first invocation.  </p>
	 *
	 * @return  The system-wide default AsynchronousChannel provider
	 */
	public static ImproveAsynchronousChannelProvider provider() {
		return ProviderHolder.provider;
	}

	/**
	 * Constructs a new asynchronous channel group with a fixed thread pool.
	 *
	 * @param   nThreads
	 *          The number of threads in the pool
	 * @param   threadFactory
	 *          The factory to use when creating new threads
	 *
	 * @return  A new asynchronous channel group
	 *
	 * @throws  IllegalArgumentException
	 *          If {@code nThreads <= 0}
	 * @throws  IOException
	 *          If an I/O error occurs
	 *
	 * @see ImproveAsynchronousChannelGroup#withFixedThreadPool
	 */
	public abstract ImproveAsynchronousChannelGroup
	openImproveAsynchronousChannelGroup(int nThreads, ThreadFactory threadFactory) throws IOException;

	/**
	 * Constructs a new asynchronous channel group with the given thread pool.
	 *
	 * @param   executor
	 *          The thread pool
	 * @param   initialSize
	 *          A value {@code >=0} or a negative value for implementation
	 *          specific default
	 *
	 * @return  A new asynchronous channel group
	 *
	 * @throws  IOException
	 *          If an I/O error occurs
	 *
	 * @see ImproveAsynchronousChannelGroup#withCachedThreadPool
	 */
	public abstract ImproveAsynchronousChannelGroup
	openImproveAsynchronousChannelGroup(ExecutorService executor, int initialSize) throws IOException;

	/**
	 * Opens an asynchronous server-socket channel.
	 *
	 * @param   group
	 *          The group to which the channel is bound, or {@code null} to
	 *          bind to the default group
	 *
	 * @return  The new channel
	 *
	 * @throws IllegalChannelGroupException
	 *          If the provider that created the group differs from this provider
	 * @throws ShutdownChannelGroupException
	 *          The group is shutdown
	 * @throws  IOException
	 *          If an I/O error occurs
	 */
	public abstract ImproveAsynchronousServerSocketChannel openImproveAsynchronousServerSocketChannel
	(ImproveAsynchronousChannelGroup group) throws IOException;

	/**
	 * Opens an asynchronous socket channel.
	 *
	 * @param   group
	 *          The group to which the channel is bound, or {@code null} to
	 *          bind to the default group
	 *
	 * @return  The new channel
	 *
	 * @throws  IllegalChannelGroupException
	 *          If the provider that created the group differs from this provider
	 * @throws  ShutdownChannelGroupException
	 *          The group is shutdown
	 * @throws  IOException
	 *          If an I/O error occurs
	 */
	public abstract ImproveAsynchronousSocketChannel openImproveAsynchronousSocketChannel
	(ImproveAsynchronousChannelGroup group) throws IOException;
}
