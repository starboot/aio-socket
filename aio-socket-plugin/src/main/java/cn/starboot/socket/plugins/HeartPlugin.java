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
package cn.starboot.socket.plugins;

import cn.starboot.socket.Packet;
import cn.starboot.socket.StateMachineEnum;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.utils.QuickTimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 *心跳插件
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class HeartPlugin extends AbstractPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartPlugin.class);

    private static final TimeoutCallback DEFAULT_TIMEOUT_CALLBACK = (context, lastTime) -> context.close(true);

    private final Map<ChannelContext, Long> sessionMap = new HashMap<>();

    private final long timeout;

    private final TimeoutCallback timeoutCallback;

    /**
     * 心跳插件
     * 心跳插件在断网场景可能会触发TCP Retransmission,导致无法感知到网络实际状态,可通过设置timeout关闭连接
     *
     * @param timeout   消息超时时间
     * @param unit      时间单位
     */
    public HeartPlugin(int timeout, TimeUnit unit) {
        this(timeout, unit, DEFAULT_TIMEOUT_CALLBACK);
    }

    /**
     * 心跳插件
     * 心跳插件在断网场景可能会触发TCP Retransmission,导致无法感知到网络实际状态,可通过设置timeout关闭连接
     *
     * @param timeout   消息超时时间
     */
    private HeartPlugin(int timeout, TimeUnit timeUnit, TimeoutCallback timeoutCallback) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout should bigger than zero");
        }
        this.timeout = timeUnit.toMillis(timeout);
        this.timeoutCallback = timeoutCallback;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("aio-socket version: " + AioConfig.VERSION + "; server kernel's heart plugin added successfully");
        }
    }

    @Override
    public final boolean beforeProcess(ChannelContext channelContext, Packet packet) {
        sessionMap.put(channelContext, System.currentTimeMillis());
        //是否心跳响应消息 延长心跳监测时间
        return !isHeartMessage(packet);
    }

    @Override
    public final void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext context, Throwable throwable) {
        switch (stateMachineEnum) {
            case NEW_CHANNEL:
                sessionMap.put(context, System.currentTimeMillis());
                registerHeart(context);
                //注册心跳监测
                break;
            case CHANNEL_CLOSED:
                //移除心跳监测
                sessionMap.remove(context);
                break;
            default:
                break;
        }
    }

    /**
     * 判断当前收到的消息是否为心跳消息。
     * 心跳请求消息与响应消息可能相同，也可能不同，因实际场景而异，故接口定义不做区分。
     *
     * @param packet 心跳包
     * @return       判断是否为心跳包
     */
    public abstract boolean isHeartMessage(Packet packet);

    private void registerHeart(final ChannelContext channelContext) {
        QuickTimerTask.SCHEDULED_EXECUTOR_SERVICE.schedule(new TimerTask() {
            @Override
            public void run() {
                if (channelContext.isInvalid()) {
                    sessionMap.remove(channelContext);
                    return;
                }
                Long lastTime = sessionMap.get(channelContext);
                if (lastTime == null) {
                    lastTime = System.currentTimeMillis();
                    sessionMap.put(channelContext, lastTime);
                }
                long current = System.currentTimeMillis();
                //超时未收到消息，关闭连接
                if (timeout > 0 && (current - lastTime) > timeout) {
                    timeoutCallback.callback(channelContext, lastTime);
                }
                registerHeart(channelContext);
            }
        }, 20000, TimeUnit.MILLISECONDS);
    }

    public interface TimeoutCallback {

        void callback(ChannelContext context, long lastTime);
    }
}
