package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.NetMonitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.core.ChannelContext;

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
    public void handle(ChannelContext channelContext, Packet packet) {
        boolean flag = true;
        for (Plugin plugin : plugins) {
            if (!plugin.preProcess(channelContext, packet)) {
                flag = false;
            }
        }
        if (flag) {
            aioHandler.handle(channelContext, packet);
        }
    }

    @Override
    public Packet decode(VirtualBuffer readBuffer, ChannelContext channelContext) {
        return aioHandler.decode(readBuffer, channelContext);
    }

    @Override
    public VirtualBuffer encode(Packet packet, ChannelContext channelContext) {
        return aioHandler.encode(packet, channelContext);
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
