package plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.plugins.Plugin;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.channels.AsynchronousSocketChannel;

public class DemoPlugins implements Plugin {


    @Override
    public boolean preProcess(ChannelContext channelContext, Packet packet) {
        System.out.println("处理消息前执行...");
        return true;
    }

    @Override
    public Packet preDecode(VirtualBuffer readBuffer, ChannelContext channelContext) {
        return null;
    }

    @Override
    public VirtualBuffer preEncode(Packet packet, ChannelContext channelContext) {
        return null;
    }

    @Override
    public void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext channelContext, Throwable throwable) {
        System.out.println("触发状态机...");
    }

    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        System.out.println("收到新连接...");
        return channel;
    }

    @Override
    public void afterRead(ChannelContext context, int readSize) {
        System.out.println("读之后...");
    }

    @Override
    public void beforeRead(ChannelContext context) {
        System.out.println("读之前...");
    }

    @Override
    public void afterWrite(ChannelContext context, int writeSize) {
        System.out.println("写之后...");
    }

    @Override
    public void beforeWrite(ChannelContext context) {
        System.out.println("写之前...");
    }
}
