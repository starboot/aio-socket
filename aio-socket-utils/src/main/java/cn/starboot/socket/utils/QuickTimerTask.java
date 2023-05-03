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
package cn.starboot.socket.utils;

import java.util.concurrent.*;

/**
 * 服务器定时任务
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class QuickTimerTask implements Runnable {

    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            new ScheduledThreadPoolExecutor(1, r -> {
                Thread thread = new Thread(r, "aio-socket Quick Timer");
                thread.setDaemon(true);
                return thread;
            });


	private final long delay;

	private final long period;

    public QuickTimerTask(long delay, long period) {
    	this.delay = delay;
    	this.period = period;
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this, getDelay(), getPeriod(), TimeUnit.MILLISECONDS);
    }

    public static void cancelQuickTask() {
        SCHEDULED_EXECUTOR_SERVICE.shutdown();
    }

    public static void scheduleAtFixedRate(Runnable command, long initialDelay, long period) {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    protected long getDelay() {
    	return this.delay;
	}

    protected long getPeriod() {
    	return this.period;
	}
}
