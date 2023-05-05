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
package cn.starboot.socket.core;

import cn.starboot.socket.Monitor;
import cn.starboot.socket.StateMachineEnum;

import java.nio.channels.CompletionHandler;

/**
 * 读完成回调函数
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
final class ReadCompletionHandler implements CompletionHandler<Integer, TCPChannelContext> {

    @Override
    public void completed(Integer result, TCPChannelContext channelContext) {
        // 读取完成,result:实际读取的字节数。如果对方关闭连接则result=-1。
        if (channelContext.aioDecoder(result)) {
            return;
        }
        try {
            // 接收到的消息进行预处理
            Monitor monitor = channelContext.getAioConfig().getMonitor();
            if (monitor != null) {
                monitor.afterRead(channelContext, result);
            }
            //触发读回调
            channelContext.signalRead(result == -1);
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
    public void failed(Throwable exc, TCPChannelContext channelContext) {
        try {
            channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.INPUT_EXCEPTION, exc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            channelContext.close(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
