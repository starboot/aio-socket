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
package io.github.mxd888.socket.utils.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractSynRunnable implements Runnable {

    private static final Logger log	= LoggerFactory.getLogger(AbstractSynRunnable.class);

    public boolean executed	= false;

    protected ReentrantLock runningLock	= new ReentrantLock();

    public final Executor executor;

    private boolean	isCanceled	= false;

    protected AbstractSynRunnable(Executor executor) {
        this.executor = executor;
    }

    public void execute() {
        executor.execute(this);
    }

    public abstract boolean isNeededExecute();

    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public final void run() {
        if (isCanceled()) {
            return;
        }
        boolean tryLock = false;
        try {
            tryLock = runningLock.tryLock(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            log.error(e1.toString(), e1);
        }
        if (tryLock) {
            try {
                int loopCount = 0;
                runTask();
                while (isNeededExecute() && loopCount++ < 100) {
                    runTask();
                }

            } catch (Throwable e) {
                log.error(e.toString(), e);
            } finally {
                executed = false;
                runningLock.unlock();
            }
        } else {
            executed = false;
        }

        if (isNeededExecute()) {
            execute();
        }

    }

    public abstract void runTask();

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public String logstr() {
        return this.getClass().getName();
    }
}
