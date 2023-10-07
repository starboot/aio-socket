package cn.starboot.socket.jdk.nio;

import cn.starboot.socket.utils.SystemPropertyUtil;
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
 * 解决一下NIO空轮训的bug(虽然官方已经表明修复成功，但部分网友仍坚持还有出现的可能)
 *
 * @author MDong
 */
public final class ImproveNioSelector extends AbstractSelector {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveNioSelector.class);

	private Selector selector;

	private static final int MIN_PREMATURE_SELECTOR_RETURNS = 10;

	private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;

	static {
		int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("cn.starboot.socket.selectorAutoRebuildThreshold", 512);
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

	private int selectCnt;
	private boolean isLoop;
	private int select0(SelectModel selectModel, long timeout) throws IOException {
		if (selectModel == SelectModel.SELECT_NOW)
			return selector.selectNow();

		initState();
		int select = 0;
		long star = System.currentTimeMillis();
		while (isLoop) {
			selectCnt++;
			select = selector.select(timeout);
			if (select > 0 || unexpectedSelectorWakeup(selectCnt)) {
				if (selectCnt > MIN_PREMATURE_SELECTOR_RETURNS && LOGGER.isDebugEnabled()) {
					LOGGER.debug("Selector.select() returned prematurely {} times in a row for Selector {}.",
							selectCnt - 1, selector);
				}
				isLoop = false;
			}
			if (timeout > 0) {
				final long end = System.currentTimeMillis();
				final long spend = end - star;
				if (spend >= timeout) {
					isLoop = false;
				}
				timeout -= spend;
				star = end;
			}
		}
		return select;
	}

	private void initState() {
		selectCnt = 0;
		isLoop = true;
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
						"NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
			}
			return true;
		}
		if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 &&
				selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
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
