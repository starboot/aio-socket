package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.NetMonitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.ChannelContext;
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
public class AioPlugins implements AioHandler, NetMonitor {

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
    public Packet decode(VirtualBuffer readBuffer, ChannelContext channelContext) {

        Packet packet = aioHandler.decode(readBuffer, channelContext);
        if (packet != null) {
            for (Plugin plugin : plugins) {
                plugin.afterDecode(packet, channelContext);
            }
        }
        return packet;
    }

    @Override
    public VirtualBuffer encode(Packet packet, ChannelContext channelContext, VirtualBuffer writeBuffer) {
        for (Plugin plugin : plugins) {
            plugin.beforeEncode(packet, channelContext, writeBuffer);
        }
        return aioHandler.encode(packet, channelContext, writeBuffer);
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

    public final void addPlugin(Plugin plugin) {
        this.plugins.add(plugin);
    }
}
