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
package io.github.mxd888.socket.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class SystemTimer {
	private static class TimerTask implements Runnable {
		@Override
		public void run() {
			currTime = System.currentTimeMillis();
			if (list != null) {
				for (TimerListener timerListener : list) {
					timerListener.onChange(currTime);
				}
			}
		}
	}

	private static volatile List<TimerListener> list = null;

	public static void addTimerListener(TimerListener timerListener) {
		if (list == null) {
			synchronized (TimerTask.class) {
				if (list == null) {
					list = new ArrayList<>();
				}
			}
		}
		list.add(timerListener);
	}

	public static interface TimerListener {
		void onChange(long currTime);
	}

	private final static ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable, "AioSystemTimer");
			thread.setDaemon(true);
			return thread;
		}
	});

	private static final long PERIOD = Long.parseLong(System.getProperty("tio.system.timer.period", "10"));

	public static volatile long currTime = System.currentTimeMillis();

	static {
		EXECUTOR.scheduleAtFixedRate(new TimerTask(), PERIOD, PERIOD, TimeUnit.MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread("TioSystemTimer-Shutdown") {
			@Override
			public void run() {
				EXECUTOR.shutdown();
			}
		});
	}

	public static long currentTimeMillis() {
		return currTime;
	}
}
