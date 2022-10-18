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
package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.Monitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.intf.Handler;

/**
 * aio-socket 插件接口
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface Plugin extends Monitor {

    /**
     * 对请求消息进行预处理，并决策是否进行后续的MessageProcessor处理。
     * 若返回false，则当前消息将被忽略。
     * 若返回true，该消息会正常秩序MessageProcessor.process.
     *
     * @param channelContext 通道上下文
     * @param packet         消息包
     * @return               是否在本服务器进行处理逻辑
     */
    boolean beforeProcess(ChannelContext channelContext, Packet packet);

    /**
     * 解码后处理
     *
     * @param packet         解码后得到的数据包
     * @param channelContext 用户通道
     */
    void afterDecode(Packet packet, ChannelContext channelContext);

    /**
     * 编码前处理
     *
     * @param packet         待编码的数据包
     * @param channelContext 用户通道
     */
    void beforeEncode(Packet packet, ChannelContext channelContext);

    /**
     * 监听状态机事件
     *
     * @param stateMachineEnum 机器状态
     * @param channelContext   通道上下文
     * @param throwable        异常处理
     * @see Handler#stateEvent(ChannelContext, StateMachineEnum, Throwable)
     */
    void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext channelContext, Throwable throwable);

}
