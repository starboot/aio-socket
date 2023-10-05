package cn.starboot.socket.jdk.nio;

import cn.starboot.socket.utils.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

/**
 * 解决一下NIO空轮训的bug(虽然官方已经表明修复成功，但部分网友仍坚持还有出现的可能)
 *
 * @author MDong
 */
final class ImproveNioSelector extends Selector {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImproveNioSelector.class);

	private Selector selector;

	private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;

	private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;

	static {
		int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("cn.starboot.socket.selectorAutoRebuildThreshold", 512);
		if (selectorAutoRebuildThreshold < MIN_PREMATURE_SELECTOR_RETURNS) {
			selectorAutoRebuildThreshold = 0;
		}

		SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("-Dio.nio.selectorAutoRebuildThreshold: {}", SELECTOR_AUTO_REBUILD_THRESHOLD);
		}
	}

	private ImproveNioSelector() {
		try {
			this.selector = openSelector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ImproveNioSelector open() throws IOException {
		return new ImproveNioSelector();
	}

	@Override
	public boolean isOpen() {
		return selector.isOpen();
	}

	@Override
	public SelectorProvider provider() {
		return selector.provider();
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
		return selector.selectNow();
	}

	@Override
	public int select(long timeout) throws IOException {
		return selector.select(timeout);
	}

	int selectCnt = 0;
	@Override
	public int select() throws IOException {
		selectCnt++;
		boolean ranTasks = true;
		if (ranTasks) {  // ranTasks || strategy > 0
			if (selectCnt > MIN_PREMATURE_SELECTOR_RETURNS && LOGGER.isDebugEnabled()) {
				LOGGER.debug("Selector.select() returned prematurely {} times in a row for Selector {}.",
						selectCnt - 1, selector);
			}
			selectCnt = 0;
		} else if (unexpectedSelectorWakeup(selectCnt)) { // Unexpected wakeup (unusual case)
			selectCnt = 0;
		}
		return selector.select();
	}

	@Override
	public Selector wakeup() {
		return selector.wakeup();
	}

	@Override
	public void close() throws IOException {
		selector.close();
	}


	// returns true if selectCnt should be reset
	private boolean unexpectedSelectorWakeup(int selectCnt) {
		if (Thread.interrupted()) {
			// Thread was interrupted so reset selected keys and break so we not run into a busy loop.
			// As this is most likely a bug in the handler of the user or it's client library we will
			// also log it.
			//
			// See /issues/2426
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

		try {
			newSelector = openSelector();
		} catch (Exception e) {
			LOGGER.warn("Failed to create a new Selector.", e);
			return;
		}

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

	private Selector openSelector() throws IOException {
		return Selector.open();
	}


}
