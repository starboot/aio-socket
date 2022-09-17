package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * 抽象aio-socket插件，如果只想监控部分行为则继承AbstractPlugin，如果想需要监控所有行为可直接实现Plugin接口
 * 如果一个子类继承了父类的方法，那么不用super关键字就是调用本类的方法，如果想调用父类的话就要加super
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class AbstractPlugin implements Plugin {

    @Override
    public boolean beforeProcess(ChannelContext channelContext, Packet packet) {
        return true;
    }

    @Override
    public void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext channelContext, Throwable throwable) {

    }

    @Override
    public void afterDecode(Packet packet, ChannelContext channelContext) {
    }

    @Override
    public void beforeEncode(Packet packet, ChannelContext channelContext, VirtualBuffer writeBuffer) {
    }

    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        return channel;
    }

    @Override
    public void afterRead(ChannelContext channelContext, int readSize) {

    }

    @Override
    public void afterWrite(ChannelContext channelContext, int writeSize) {

    }

    @Override
    public void beforeRead(ChannelContext channelContext) {

    }

    @Override
    public void beforeWrite(ChannelContext channelContext) {

    }
}
