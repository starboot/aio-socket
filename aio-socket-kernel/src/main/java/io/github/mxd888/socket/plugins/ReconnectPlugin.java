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
package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.TCPChannelContext;
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
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's reconnect plugin added successfully");
    }

    @Override
    public void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext context, Throwable throwable) {
        if (stateMachineEnum != StateMachineEnum.CHANNEL_CLOSED) {
            return;
        }
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; client kernel starting reconnect");
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
