package io.github.mxd888.socket.core;

import io.github.mxd888.socket.NetMonitor;
import io.github.mxd888.socket.StateMachineEnum;

import java.nio.channels.CompletionHandler;

/**
 * 写完成回调函数
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, TCPChannelContext> {

    @Override
    public void completed(Integer result, TCPChannelContext channelContext) {
        try {
            NetMonitor monitor = channelContext.getAioConfig().getMonitor();
            if (monitor != null) {
                monitor.afterWrite(channelContext, result);
            }
            channelContext.writeCompleted();
        } catch (Exception e) {
            failed(e, channelContext);
        }
    }

    @Override
    public void failed(Throwable exc, TCPChannelContext channelContext) {
        try {
            channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.OUTPUT_EXCEPTION, exc);
            channelContext.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
