package cn.starboot.socket.core;

import cn.starboot.socket.core.enums.StateMachineEnum;

import java.util.concurrent.Executor;

/**
 * 消息解码逻辑执行器
 * 异步aio-socket工作执行者
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class AsyAioWorker implements Runnable {

	private final Executor executor;

	private boolean	isCanceled	= false;

	private Integer result;

	private final ChannelContext channelContext;

	protected AsyAioWorker(ChannelContext channelContext, Executor executor) {
		this.executor = executor;
		this.channelContext = channelContext;
	}

	protected void execute() {
		executor.execute(this);
	}

	@Override
	public void run() {
		if (isCanceled()) {
			return;
		}
		decode(getResult());
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void setCanceled(boolean canceled) {
		this.isCanceled = canceled;
	}

	private Integer getResult() {
		return result;
	}

	protected AsyAioWorker setResult(Integer result) {
		this.result = result;
		return this;
	}

	private AioConfig getAioConfig() {
		return this.channelContext.getAioConfig();
	}

	private void decode(Integer result) {
		try {
			Monitor monitor = getAioConfig().getMonitor();
			if (monitor != null) {
				monitor.afterRead(this.channelContext, result);
			}
			this.channelContext.signalRead(result == -1);
		}catch (Exception exc) {
			getAioConfig().getHandler().stateEvent(this.channelContext, StateMachineEnum.INPUT_EXCEPTION, exc);
			this.channelContext.close(false);
		}

	}
}
