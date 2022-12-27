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
package cn.starboot.socket.task;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.utils.queue.AioFullNotifyQueue;
import cn.starboot.socket.utils.queue.AioQueue;
import cn.starboot.socket.Packet;
import cn.starboot.socket.intf.Handler;
import cn.starboot.socket.utils.pool.thread.AbstractQueueRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 消息发送逻辑执行器
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class SendTask extends AbstractQueueRunnable<Packet> {

    private static final Logger LOGGER	= LoggerFactory.getLogger(SendTask.class);

    private final ChannelContext channelContext;

    private final Handler aioHandler;

    private final Consumer<Boolean> consumer;

    private AioQueue<Packet> msgQueue = null;

    public SendTask(ChannelContext channelContext, Executor executor, Consumer<Boolean> consumer) {
        super(executor);
        this.consumer = consumer;
        this.channelContext = channelContext;
        this.aioHandler = channelContext.getAioConfig().getHandler();
        getTaskQueue();
    }

    @Override
    public boolean addTask(Packet packet) {
        if (this.isCanceled()) {
            LOGGER.info("{}, 任务已经取消，{}添加到发送队列失败", channelContext, packet.getReq());
            return false;
        }

        return msgQueue.offer(packet);
    }

    /**
     * 编码，编进内存池中
     * @param packet 数据包
     */
    private void getByteBuffer(Packet packet) {
        try {
            synchronized (this.channelContext) {
                aioHandler.encode(packet, channelContext);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void runTask() {
        if (msgQueue.isEmpty()) {
            return;
        }

        Packet packet;
        while ((packet = msgQueue.poll()) != null) {
            getByteBuffer(packet);
        }
        if (channelContext.isInvalid()) {
            return;
        }
        // 发送
        consumer.accept(true);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + channelContext.toString();
    }

    @Override
    public AioQueue<Packet> getTaskQueue() {
        if (msgQueue == null) {
            synchronized (this) {
                if (msgQueue == null) {
                    msgQueue = new AioFullNotifyQueue<>(channelContext.getAioConfig().getMaxWaitNum());
                }
            }
        }
        return msgQueue;
    }

}
