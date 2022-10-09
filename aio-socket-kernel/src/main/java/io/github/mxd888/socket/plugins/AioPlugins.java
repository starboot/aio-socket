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

import io.github.mxd888.socket.Monitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;
import io.github.mxd888.socket.intf.AioHandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * aio-socket 插件嵌入类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class AioPlugins implements AioHandler, Monitor {

    private AioHandler aioHandler;

    private final List<Plugin> plugins = new ArrayList<>();

    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        AsynchronousSocketChannel acceptChannel = channel;
        for (Plugin plugin : plugins) {
            acceptChannel = plugin.shouldAccept(acceptChannel);
            if (acceptChannel == null) {
                return null;
            }
        }
        return acceptChannel;
    }

    @Override
    public void afterRead(ChannelContext context, int readSize) {
        for (Plugin plugin : plugins) {
            plugin.afterRead(context, readSize);
        }
    }

    @Override
    public void beforeRead(ChannelContext context) {
        for (Plugin plugin : plugins) {
            plugin.beforeRead(context);
        }
    }

    @Override
    public void afterWrite(ChannelContext context, int writeSize) {
        for (Plugin plugin : plugins) {
            plugin.afterWrite(context, writeSize);
        }
    }

    @Override
    public void beforeWrite(ChannelContext context) {
        for (Plugin plugin : plugins) {
            plugin.beforeWrite(context);
        }
    }

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        boolean flag = true;
        for (Plugin plugin : plugins) {
            if (!plugin.beforeProcess(channelContext, packet)) {
                flag = false;
            }
        }
        if (flag) {
            return aioHandler.handle(channelContext, packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {

        Packet packet = aioHandler.decode(readBuffer, channelContext);
        if (packet != null) {
            for (Plugin plugin : plugins) {
                plugin.afterDecode(packet, channelContext);
            }
        }
        return packet;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {
        for (Plugin plugin : plugins) {
            plugin.beforeEncode(packet, channelContext);
        }
        aioHandler.encode(packet, channelContext);
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        for (Plugin plugin : plugins) {
            plugin.stateEvent(stateMachineEnum, channelContext, throwable);
        }
        aioHandler.stateEvent(channelContext, stateMachineEnum, throwable);
    }

    public void setAioHandler(AioHandler aioHandler) {
        this.aioHandler = aioHandler;
    }

    public final AioPlugins addPlugin(Plugin plugin) {
        this.plugins.add(plugin);
        return this;
    }
}
