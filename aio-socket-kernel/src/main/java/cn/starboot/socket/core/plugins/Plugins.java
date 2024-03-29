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

import cn.starboot.socket.core.Monitor;
import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.enums.ProtocolEnum;
import cn.starboot.socket.core.enums.StateMachineEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.exception.AioEncoderException;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.intf.Handler;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.core.exception.AioDecoderException;

import java.util.*;

/**
 * aio-socket 插件嵌入类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Plugins implements Handler, Monitor {

    /**
     * 服务器第一个协议解析器
     */
	private ProtocolEnum serverDefaultFirstProtocol = null;

    /**
     * 插件项
     */
    private final List<Plugin> aioPluginList = new ArrayList<>();

    /**
     * 处理器集合
     */
    private final Map<ProtocolEnum, AioHandler> handlers = new HashMap<>();

    @Override
    public ImproveAsynchronousSocketChannel agreeAccept(ImproveAsynchronousSocketChannel asynchronousSocketChannel) {
        for (Plugin p : aioPluginList) {
            if (p.agreeAccept(asynchronousSocketChannel) == null) {
                return null;
            }
        }
        return asynchronousSocketChannel;
    }

    @Override
    public void afterRead(ChannelContext context, int readSize) {
        for (Plugin p : aioPluginList) {
            p.afterRead(context, readSize);
        }
    }

    @Override
    public void beforeRead(ChannelContext context) {
        for (Plugin p : aioPluginList) {
            p.beforeRead(context);
        }
    }

    @Override
    public void afterWrite(ChannelContext context, int writeSize) {
        for (Plugin p : aioPluginList) {
            p.afterWrite(context, writeSize);
        }
    }

    @Override
    public void beforeWrite(ChannelContext context) {
        for (Plugin p : aioPluginList) {
            p.beforeWrite(context);
        }
    }

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        boolean flag = true;
        for (Plugin p : aioPluginList) {
            if (!p.beforeProcess(channelContext, packet)) {
                flag = false;
            }
        }
        return flag ? handlers.get(channelContext.getProtocol()).handle(channelContext, packet) : null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
        Packet packet = null;
        ProtocolEnum protocol = channelContext.getProtocol();
        if (protocol != null) {
            packet = handlers.get(protocol).decode(readBuffer, channelContext);
        }else {
			for (AioHandler handler : handlers.values()) {
				packet = handler.decode(readBuffer, channelContext);
				if (packet != null) {
					// 解码成功，设置协议。中断链式解码
					channelContext.setProtocol(handler.name());
					break;
				}
			}
        }
        if (packet != null) {
            for (Plugin p : aioPluginList) {
                p.afterDecode(packet, channelContext);
            }
        }
        return packet;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
        for (Plugin p : aioPluginList) {
            p.beforeEncode(packet, channelContext);
        }
        handlers.get(channelContext.getProtocol()).encode(packet, channelContext);
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        for (Plugin p : aioPluginList) {
            p.stateEvent(stateMachineEnum, channelContext, throwable);
        }
        if (channelContext.getProtocol() != null) {
            handlers.get(channelContext.getProtocol()).stateEvent(channelContext, stateMachineEnum, throwable);
            return;
        }
        // 当前通道未确定协议就触发状态机，则使用第一个处理器进行处理
		handlers.get(serverDefaultFirstProtocol).stateEvent(channelContext, stateMachineEnum, throwable);
    }

    public void addAioHandler(AioHandler handler) {
        handlers.put(handler.name(), handler);
        if (serverDefaultFirstProtocol == null) {
        	serverDefaultFirstProtocol = handler.name();
		}
    }

    public final Plugins addPlugin(Plugin plugin) {
        this.aioPluginList.add(plugin);
        return this;
    }
}
