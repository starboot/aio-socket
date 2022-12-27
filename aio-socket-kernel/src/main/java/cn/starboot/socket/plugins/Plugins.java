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

import cn.starboot.socket.Monitor;
import cn.starboot.socket.Packet;
import cn.starboot.socket.ProtocolEnum;
import cn.starboot.socket.StateMachineEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.intf.Handler;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.exception.AioDecoderException;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;

/**
 * aio-socket 插件嵌入类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Plugins implements Handler, Monitor {

    /**
     * 责任链头指针
     */
    private final LinkedList<AioHandler> aioHandler = new LinkedList<>();

    /**
     * 插件项
     */
    private final List<Plugin> plugins = new ArrayList<>();

    /**
     * 处理器集合
     */
    private final Map<ProtocolEnum, AioHandler> handlers = new HashMap<>();

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
            return handlers.get(channelContext.getProtocol()).handle(channelContext, packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
        Packet packet = null;
        ProtocolEnum protocol = channelContext.getProtocol();
        if (protocol != null) {
            packet = handlers.get(protocol).decode(readBuffer, channelContext);
        }else {
			for (AioHandler handler : aioHandler) {
				packet = handler.decode(readBuffer, channelContext);
				if (packet != null) {
					// 解码成功，设置协议。中断链式解码
					channelContext.setProtocol(handler.name());
					break;
				}
			}
        }
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
        handlers.get(channelContext.getProtocol()).encode(packet, channelContext);
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        for (Plugin plugin : plugins) {
            plugin.stateEvent(stateMachineEnum, channelContext, throwable);
        }
        if (channelContext.getProtocol() != null) {
            handlers.get(channelContext.getProtocol()).stateEvent(channelContext, stateMachineEnum, throwable);
            return;
        }
        // 当前通道未确定协议就触发状态机，则使用头处理器进行处理
		aioHandler.getFirst().stateEvent(channelContext, stateMachineEnum, throwable);
    }

    public void addAioHandler(AioHandler handler) {
        handlers.put(handler.name(), handler);
        aioHandler.addLast(handler);
    }

    public final Plugins addPlugin(Plugin plugin) {
        this.plugins.add(plugin);
        return this;
    }
}
