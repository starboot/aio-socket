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
package io.github.mxd888.socket.intf;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

/**
 * 消息解编码处理类，并包含了消息处理，状态机触发回调
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface AioHandler<T> {

    /**
     * 消息处理回调方法
     *
     * @param channelContext 用户上下文
     * @param packet         消息包
     * @return               返回Packet消息包
     */
    Packet<T> handle(ChannelContext channelContext, Packet<T> packet);

    /**
     * 解码回调方法
     *
     * @param readBuffer            读到的buffer流
     * @param channelContext        用户上下文
     * @return                      返回Packet消息包
     * @throws AioDecoderException  解码异常
     */
    Packet<T> decode(final MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException;

    /**
     * 编码回调方法
     *
     * @param packet         需要编码消息包
     * @param channelContext 用户上下文
     */
    void encode(Packet<T> packet, ChannelContext channelContext);

    /**
     * 状态机事件,当枚举事件发生时由框架触发该方法
     *
     * @param channelContext                            本次触发状态机的AioSession对象
     * @param stateMachineEnum                          状态枚举
     * @param throwable                                 异常对象，如果存在的话
     * @see io.github.mxd888.socket.StateMachineEnum    状态机枚举
     */
    default void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        if (stateMachineEnum == StateMachineEnum.DECODE_EXCEPTION || stateMachineEnum == StateMachineEnum.PROCESS_EXCEPTION) {
            throwable.printStackTrace();
        }
    }
}
