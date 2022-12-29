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
package cn.starboot.socket.intf;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.Packet;
import cn.starboot.socket.exception.AioDecoderException;

/**
 * 消息解编码处理类，并包含了消息处理，状态机触发回调
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface Handler extends StateMachineEvent{

    /**
     * 消息处理回调方法
     *
     * @param channelContext 用户上下文
     * @param packet         消息包
     * @return               返回Packet消息包
     */
    Packet handle(ChannelContext channelContext, Packet packet);

    /**
     * 解码回调方法
     *
     * @param readBuffer            读到的buffer流
     * @param channelContext        用户上下文
     * @return                      返回Packet消息包
     * @throws AioDecoderException  解码异常
     */
    Packet decode(final MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException;

    /**
     * 编码回调方法
     *
     * @param packet         需要编码消息包
     * @param channelContext 用户上下文
     */
    void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException;

}
