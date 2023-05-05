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
 * 写完成回调函数
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
final class WriteCompletionHandler implements CompletionHandler<Integer, TCPChannelContext> {

    @Override
    public void completed(Integer result, TCPChannelContext channelContext) {
        try {
            Monitor monitor = channelContext.getAioConfig().getMonitor();
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
            channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.ENCODE_EXCEPTION, exc);
            channelContext.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
