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

import io.github.mxd888.socket.utils.queue.AioQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public abstract class AbstractQueueRunnable<T> extends AbstractAioRunnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueueRunnable.class);

    protected AbstractQueueRunnable(Executor executor) {
        super(executor);
    }

    protected AbstractQueueRunnable(Executor executor, int maxExecuteNum) {
        super(executor, maxExecuteNum);
    }

    /**
     * 添加消息
     *
     * @return 添加任务
     */
    public boolean addTask(T t) {
        if (this.isCanceled()) {
            LOGGER.error("task has been cancel");
            return false;
        }
        return getTaskQueue().offer(t);
    }

    /**
     * 清空处理的队列消息
     */
    public void clearTaskQueue() {
        if (getTaskQueue() != null) {
            getTaskQueue().clear();
        }
    }

    @Override
    public boolean isNeededExecute() {
        return  (getTaskQueue() != null && !getTaskQueue().isEmpty()) && !this.isCanceled();
    }

    /**
     * 获取消息队列
     *
     * @return 消息队列
     */
    public abstract AioQueue<T> getTaskQueue();
}
