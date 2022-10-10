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
package io.github.mxd888.socket.task;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.pool.thread.AbstractQueueRunnable;
import io.github.mxd888.socket.utils.queue.AioFullWaitQueue;
import io.github.mxd888.socket.utils.queue.AioQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * 消息发送逻辑执行器
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class SendRunnable extends AbstractQueueRunnable<Packet> {

    private static final Logger LOGGER	= LoggerFactory.getLogger(SendRunnable.class);

    private final ChannelContext channelContext;

    private final AioHandler aioHandler;

    private AioQueue<Packet> msgQueue = null;

    public SendRunnable(ChannelContext channelContext, Executor executor) {
        super(executor);
        this.channelContext = channelContext;
        this.aioHandler = channelContext.getAioConfig().getHandler();
        getMsgQueue();
    }

    @Override
    public boolean addMsg(Packet packet) {
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
            aioHandler.encode(packet, channelContext);
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
        channelContext.getWriteBuffer().flush();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + channelContext.toString();
    }

    @Override
    public AioQueue<Packet> getMsgQueue() {
        if (msgQueue == null) {
            synchronized (this) {
                if (msgQueue == null) {
                    msgQueue = new AioFullWaitQueue<>(channelContext.getAioConfig().getMaxWaitNum());
                }
            }
        }
        return msgQueue;
    }

}