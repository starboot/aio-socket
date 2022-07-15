package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.utils.QuickTimerTask;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * 重连插件 客户端使用
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ReconnectPlugin extends AbstractPlugin {

    private final AsynchronousChannelGroup asynchronousChannelGroup;

    private final ClientBootstrap client;

    public ReconnectPlugin(ClientBootstrap client) {
        this(client, null);
    }

    public ReconnectPlugin(ClientBootstrap client, AsynchronousChannelGroup asynchronousChannelGroup) {
        this.client = client;
        this.asynchronousChannelGroup = asynchronousChannelGroup;
    }

    @Override
    public void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext context, Throwable throwable) {
        if (stateMachineEnum != StateMachineEnum.CHANNEL_CLOSED) {
            return;
        }
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + ";client kernel starting reconnect");
        QuickTimerTask.SCHEDULED_EXECUTOR_SERVICE.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    client.setCheck(false);
                    if (asynchronousChannelGroup == null) {
                        client.start();
                    } else {
                        client.start(asynchronousChannelGroup);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000, TimeUnit.MILLISECONDS);
    }
}
