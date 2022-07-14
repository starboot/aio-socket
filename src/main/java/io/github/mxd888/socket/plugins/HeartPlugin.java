package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.QuickTimerTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by DELL(mxd) on 2022/7/13 19:09
 */
public class HeartPlugin extends AbstractPlugin {

    private static final TimeoutCallback DEFAULT_TIMEOUT_CALLBACK = (context, lastTime) -> context.close(true);

    private final Map<ChannelContext, Long> sessionMap = new HashMap<>();
    /**
     * 心跳频率
     */
    private long heartRate;
    /**
     * 在超时时间内未收到消息,关闭连接。
     */
    private long timeout;

    private TimeoutCallback timeoutCallback;

    /**
     * 心跳插件
     *
     * @param heartRate 心跳触发频率
     * @param timeUnit  heatRate单位
     */
    public HeartPlugin(int heartRate, TimeUnit timeUnit) {
        this(heartRate, 0, timeUnit);
    }

    /**
     * 心跳插件
     * <p>
     * 心跳插件在断网场景可能会触发TCP Retransmission,导致无法感知到网络实际状态,可通过设置timeout关闭连接
     * </p>
     *
     * @param heartRate 心跳触发频率
     * @param timeout   消息超时时间
     * @param unit      时间单位
     */
    public HeartPlugin(int heartRate, int timeout, TimeUnit unit) {
        this(heartRate, timeout, unit, DEFAULT_TIMEOUT_CALLBACK);
    }

    /**
     * 心跳插件
     * <p>
     * 心跳插件在断网场景可能会触发TCP Retransmission,导致无法感知到网络实际状态,可通过设置timeout关闭连接
     * </p>
     *
     * @param heartRate 心跳触发频率
     * @param timeout   消息超时时间
     */
    public HeartPlugin(int heartRate, int timeout, TimeUnit timeUnit, TimeoutCallback timeoutCallback) {
        if (timeout > 0 && heartRate >= timeout) {
            throw new IllegalArgumentException("heartRate must little then timeout");
        }
        this.heartRate = timeUnit.toMillis(heartRate);
        this.timeout = timeUnit.toMillis(timeout);
        this.timeoutCallback = timeoutCallback;
    }

    @Override
    public final boolean preProcess(ChannelContext channelContext, Packet packet) {
        sessionMap.put(channelContext, System.currentTimeMillis());
        //是否心跳响应消息 延长心跳监测时间
        return !isHeartMessage(channelContext, packet);
    }

    @Override
    public final void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext context, Throwable throwable) {
        switch (stateMachineEnum) {
            case NEW_CHANNEL:
                sessionMap.put(context, System.currentTimeMillis());
                registerHeart(context, heartRate);
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
     * 自定义心跳消息并发送
     *
     * @param context 用户上下文
     */
    public void sendHeartRequest(ChannelContext context) {
        Packet packet = new Packet();
        packet.setFromId(context.getId());
        packet.setToId(context.getId());
        Aio.send(context, packet);
    };

    /**
     * 判断当前收到的消息是否为心跳消息。
     * 心跳请求消息与响应消息可能相同，也可能不同，因实际场景而异，故接口定义不做区分。
     *
     * @param channelContext
     * @param packet
     * @return
     */
    public boolean isHeartMessage(ChannelContext channelContext, Packet packet) {
        return packet.getFromId().equals(packet.getToId());
    };

    private void registerHeart(final ChannelContext channelContext, final long heartRate) {
        if (heartRate <= 0) {
            return;
        }
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
                //超时未收到消息,尝试发送心跳消息
                else if (current - lastTime > heartRate) {
                    sendHeartRequest(channelContext);
                }
                registerHeart(channelContext, heartRate);
            }
        }, heartRate, TimeUnit.MILLISECONDS);
    }

    public interface TimeoutCallback {

        void callback(ChannelContext context, long lastTime);
    }
}
