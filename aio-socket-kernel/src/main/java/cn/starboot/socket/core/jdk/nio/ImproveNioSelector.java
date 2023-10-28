package cn.starboot.socket.core.jdk.nio;

import cn.starboot.socket.core.utils.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.util.Set;

/**
 * Improve Selector implementation based on the Linux epoll facility.
 *
 * 解决一下NIO空轮训的bug(虽然官方已经表明修复成功，但部分网友仍坚持还有出现的可能)
 *
 * 通过适配器设计模式进行改进一下JDK默认Selector实现类
 *
 * Linux kernel code for Epoll.c
 *
 * epoll结构体:
 * typedef union epoll_data {
 *     void *ptr;
 *     int fd;
 *     __uint32_t u32;
 *     __uint64_t u64;
 *  } epoll_data_t;
 *
 * struct epoll_event {
 *     __uint32_t events;
 *     epoll_data_t data;
 * }
 *
 * Linux系统调用(用户态切换至内核态): epoll_create function implementation
 * SYSCALL_DEFINE1(epoll_create1, int, flags)
 * {
 * 	return do_epoll_create(flags);
 * }
 *
 * // Open an eventpoll file descriptor.
 * static int do_epoll_create(int flags);
 *
 *
 * Linux系统调用(用户态切换至内核态): epoll_ctl function implementation
 * SYSCALL_DEFINE4(epoll_ctl, int, epfd, int, op, int, fd,
 * 		struct epoll_event __user *, event)
 * {
 * 	struct epoll_event epds;
 *
 * 	if (ep_op_has_event(op) &&
 * 	    copy_from_user(&epds, event, sizeof(struct epoll_event)))
 * 		return -EFAULT;
 *
 * 	return do_epoll_ctl(epfd, op, fd, &epds, false);
 * }
 *
 * // The following function implements the controller interface for
 * // the eventpoll file that enables the insertion/removal/change of
 * // file descriptors inside the interest set.
 * int do_epoll_ctl(int epfd, int op, int fd, struct epoll_event *epds,
 * 		 bool nonblock);
 *
 *
 * Linux系统调用(用户态切换至内核态): epoll_wait function implementation
 * SYSCALL_DEFINE4(epoll_wait, int, epfd, struct epoll_event __user *, events,
 * 		int, maxevents, int, timeout)
 * {
 * 	struct timespec64 to;
 *
 * 	return do_epoll_wait(epfd, events, maxevents,
 * 			     ep_timeout_to_timespec(&to, timeout));
 * }
 *
 * // Implement the event wait interface for the eventpoll file. It is the kernel
 * // part of the user space epoll_wait(2).
 *
 * static int do_epoll_wait(int epfd, struct epoll_event __user *events,
 * 			 int maxevents, struct timespec64 *to);
 *
 * @author MDong And Netty
 */
public final class ImproveNioSelector extends AbstractSelector {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveNioSelector.class);

	private Selector selector;

	private static final int MIN_PREMATURE_SELECTOR_RETURNS = 10;

	private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;

	static {
		int selectorAutoRebuildThreshold
				= SystemPropertyUtil
				.getInt("cn.starboot.socket.core.selectorAutoRebuildThreshold",
						512);
		if (selectorAutoRebuildThreshold < MIN_PREMATURE_SELECTOR_RETURNS) {
			selectorAutoRebuildThreshold = 0;
		}

		SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("-Dio.jdk.nio.selectorAutoRebuildThreshold: {}", SELECTOR_AUTO_REBUILD_THRESHOLD);
		}
	}

	private ImproveNioSelector(Selector selector) {
		super(selector.provider());
		this.selector = selector;
	}

	public static ImproveNioSelector open() {
		return new ImproveNioSelector(openSelector());
	}

	@Override
	protected SelectionKey register(AbstractSelectableChannel ch, int ops, Object att) {
		SelectionKey register = null;
		try {
			register = ch.register(selector, ops, att);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}
		return register;
	}

	@Override
	public Set<SelectionKey> keys() {
		return selector.keys();
	}

	@Override
	public Set<SelectionKey> selectedKeys() {
		return selector.selectedKeys();
	}

	@Override
	public int selectNow() throws IOException {
		return select0(SelectModel.SELECT_NOW);
	}

	@Override
	public int select(long timeout) throws IOException {
		return select0(SelectModel.SELECT, timeout);
	}

	@Override
	public int select() throws IOException {
		return select0(SelectModel.SELECT);
	}

	private int select0(SelectModel selectModel) throws IOException {
		return select0(selectModel, 0);
	}

	private boolean isLoop;
	private int select0(SelectModel selectModel, long timeout) throws IOException {
		if (selectModel == SelectModel.SELECT_NOW) {
			return selector.selectNow();
		}

		isLoop = true;
		int selectCnt = 0;
		int select = 0;
		long star = (timeout > 0) ? System.currentTimeMillis() : 0;
		while (isLoop) {
			selectCnt++;
			select = selector.select(timeout);
			if (timeout > 0)
			{
				long end = System.currentTimeMillis();
				long spend = end - star;
				if (spend >= timeout) {
					isLoop = false;
				}
				timeout -= spend;
				star = end;
			}
			if (select > 0
					|| selectedKeys().size() > 0
					|| unexpectedSelectorWakeup(selectCnt))
			{
				if (selectCnt > MIN_PREMATURE_SELECTOR_RETURNS
						&& LOGGER.isDebugEnabled()) {
					LOGGER.debug("Selector.select() returned prematurely {} times in a row for Selector {}.",
							selectCnt - 1,
							selector);
				}
				isLoop = false;
			}
		}
		return select;
	}

	@Override
	public Selector wakeup() {
		isLoop = false;
		return selector.wakeup();
	}

	@Override
	protected void implCloseSelector() throws IOException {
		selector.close();
	}


	// returns true if selectCnt should be reset
	private boolean unexpectedSelectorWakeup(int selectCnt) {
		if (Thread.interrupted()) {
			// Thread was interrupted so reset selected keys and break so we not run into a busy loop.
			// As this is most likely a bug in the handler of the user or it's client library we will
			// also log it.
			//
			// See netty/issues/2426
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Selector.select() returned prematurely because " +
						"Thread.currentThread().interrupt() was called. Use " +
						"ImproveNioSelector.shutdownGracefully() to shutdown the ImproveNioSelector.");
			}
			return true;
		}
		if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0
				&& selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
			// The selector returned prematurely many times in a row.
			// Rebuild the selector to work around the problem.
			LOGGER.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.",
					selectCnt, selector);
			rebuildSelector();
			return true;
		}
		return false;
	}

	private void rebuildSelector() {
		final Selector oldSelector = selector;
		final Selector newSelector;

		if (oldSelector == null) {
			return;
		}

		newSelector = openSelector();

		// Register all channels to the new Selector.
		int nChannels = 0;
		for (SelectionKey key: oldSelector.keys()) {
			Object a = key.attachment();
			try {
				if (!key.isValid() || key.channel().keyFor(newSelector) != null) {
					continue;
				}

				int interestOps = key.interestOps();
				key.cancel();
				key.channel().register(newSelector, interestOps, a);
				nChannels ++;
			} catch (Exception e) {
				LOGGER.warn("Failed to re-register a Channel to the new Selector.", e);
			}
		}

		selector = newSelector;

		try {
			// time to close the old selector as everything else is registered to the new one
			oldSelector.close();
		} catch (Throwable t) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Failed to close the old Selector.", t);
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Migrated " + nChannels + " channel(s) to the new Selector.");
		}
	}

	private static Selector openSelector() {
		Selector selector = null;
		try {
			selector =  Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return selector;
	}

	enum SelectModel {
		SELECT,
		SELECT_NOW
	}


}
