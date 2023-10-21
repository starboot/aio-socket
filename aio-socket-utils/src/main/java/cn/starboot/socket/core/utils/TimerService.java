/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.core.utils;

import java.util.concurrent.*;

/**
 * 服务器定时任务
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class TimerService implements Runnable {

	private static final boolean daemon = true;

	private static final int defaultCoreNum = 1;

	private static final String defaultThreadName = "aio-socket Quick Timer";

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = getScheduledExecutorService(defaultCoreNum, defaultThreadName);

	private final boolean useInternal;

	private final ScheduledExecutorService internalScheduledExecutorService;

	public TimerService(long delay, long period) {
		this(delay, period, false);
	}

	public TimerService(long delay, long period, boolean useInternal) {
		this(delay, period, useInternal, defaultThreadName);
	}

	public TimerService(long delay, long period, boolean useInternal, String threadName) {
		this(delay, period, useInternal, threadName, defaultCoreNum);
	}

	public TimerService(long delay, long period, boolean useInternal, String threadName, int coreNum) {
		this.useInternal = useInternal;
		if (this.useInternal) {
			internalScheduledExecutorService = getScheduledExecutorService(coreNum, threadName);
		}else {
			internalScheduledExecutorService = SCHEDULED_EXECUTOR_SERVICE;
		}
		internalScheduledExecutorService.scheduleAtFixedRate(this, delay, period, TimeUnit.MILLISECONDS);
	}

	protected void shutdown() {
		if (this.useInternal) {
			this.internalScheduledExecutorService.shutdown();
		}
	}

	protected void shutdownNow() {
		if (this.useInternal) {
			this.internalScheduledExecutorService.shutdownNow();
		}
	}

    public static ScheduledExecutorService getInstance() {
		return SCHEDULED_EXECUTOR_SERVICE;
	}

    public static void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period) {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(runnable, initialDelay, period, TimeUnit.MILLISECONDS);
    }

	private static ScheduledExecutorService getScheduledExecutorService(int core, String name) {
		return new ScheduledThreadPoolExecutor(core, r -> {
			Thread thread = new Thread(r, name);
			thread.setDaemon(daemon);
			return thread;
		});
	}
}
