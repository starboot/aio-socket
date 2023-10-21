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
package cn.starboot.socket.core.plugins;

import cn.starboot.socket.core.enums.StateMachineEnum;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.tcp.ClientBootstrap;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousChannelGroup;
import cn.starboot.socket.core.utils.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * 重连插件 客户端使用
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ReconnectPlugin extends AbstractPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectPlugin.class);

    private final ImproveAsynchronousChannelGroup asynchronousChannelGroup;

    private final ClientBootstrap client;

	private final long period;

    public ReconnectPlugin(ClientBootstrap client) {
        this(client, 2, TimeUnit.SECONDS);
    }

	public ReconnectPlugin(ClientBootstrap client, int period, TimeUnit timeUnit) {
		this(client, period, timeUnit, null);
	}

    public ReconnectPlugin(ClientBootstrap client, int period, TimeUnit timeUnit, ImproveAsynchronousChannelGroup asynchronousChannelGroup) {
        this.client = client;
		this.period =  timeUnit.toMillis(period);
        this.asynchronousChannelGroup = asynchronousChannelGroup;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("aio-socket version: " + AioConfig.VERSION + "; server kernel's reconnect plugin added successfully");
        }
    }

    @Override
    public void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext context, Throwable throwable) {
        if (stateMachineEnum != StateMachineEnum.CHANNEL_CLOSED) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("aio-socket "+"version: " + AioConfig.VERSION + "; client kernel starting reconnect");
        }
        TimerService.getInstance().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
					client.start(asynchronousChannelGroup);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, this.period, TimeUnit.MILLISECONDS);
    }
}
