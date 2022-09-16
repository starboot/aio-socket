package io.github.mxd888.socket.core;

import io.github.mxd888.socket.NetMonitor;
import io.github.mxd888.socket.StateMachineEnum;

import java.nio.channels.CompletionHandler;

/**
 * 读完成回调函数
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
final class ReadCompletionHandler implements CompletionHandler<Integer, ChannelContext> {

    /**
     * 只在本包下可以被调用
     */
    ReadCompletionHandler() {
    }


    @Override
    public void completed(Integer result, ChannelContext channelContext) {
        // 读取完成,result:实际读取的字节数。如果对方关闭连接则result=-1。
        try {
            // 接收到的消息进行预处理
            NetMonitor monitor = channelContext.getAioConfig().getMonitor();
            if (monitor != null) {
                monitor.afterRead(channelContext, result);
            }
            //触发读回调
            channelContext.flipRead(result == -1);
            channelContext.signalRead();
        } catch (Exception e) {
            failed(e, channelContext);
        }
    }

    /**
     * 读失败，他会在什么时候触发呢？（忽略completed方法中异常后的调用）
     * 1. 会在客户端机器主动杀死aio-socket客户端进程或者客户端机器突然宕机或坏掉时，则服务器端对应的ChannelContext会调用此方法
     * 2. 相比之下，当客户端的ChannelContext正在读通道时，服务器关闭了对应的连接，则客户端的ChannelContext会调用此方法
     * @param exc            异常信息
     * @param channelContext 读完成出错的通道
     */
    @Override
    public void failed(Throwable exc, ChannelContext channelContext) {
        try {
            channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.INPUT_EXCEPTION, exc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //兼容性处理，windows要强制关闭,其他系统优雅关闭
            //channelContext.close(IOUtil.OS_WINDOWS);
            channelContext.close(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
