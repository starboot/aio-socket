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

    @Override
    public void failed(Throwable exc, ChannelContext channelContext) {
        try {
            channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.INPUT_EXCEPTION, exc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //兼容性处理，windows要强制关闭,其他系统优雅关闭
            //aioSession.close(IOUtil.OS_WINDOWS);
            channelContext.close(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
