package io.github.mxd888.socket.intf;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.ChannelContext;

/**
 * 消息解编码处理类，并包含了消息处理，状态机触发回调
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface AioHandler {

    /**
     * 消息处理回调方法
     *
     * @param channelContext 用户上下文
     * @param packet         消息包
     */
    void handle(ChannelContext channelContext, Packet packet);

    /**
     * 解码回调方法
     *
     * @param readBuffer     读到的buffer流
     * @param channelContext 用户上下文
     * @return               返回Packet消息包
     */
    Packet decode(final VirtualBuffer readBuffer, ChannelContext channelContext, Packet packet);

    /**
     * 编码回调方法
     *
     * @param packet         需要编码消息包
     * @param channelContext 用户上下文
     * @return               buffer流
     */
    VirtualBuffer encode(Packet packet, ChannelContext channelContext, VirtualBuffer writeBuffer);

    /**
     * 状态机事件,当枚举事件发生时由框架触发该方法
     *
     * @param channelContext   本次触发状态机的AioSession对象
     * @param stateMachineEnum 状态枚举
     * @param throwable        异常对象，如果存在的话
     * @see io.github.mxd888.socket.StateMachineEnum
     */
    default void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        if (stateMachineEnum == StateMachineEnum.DECODE_EXCEPTION || stateMachineEnum == StateMachineEnum.PROCESS_EXCEPTION) {
            throwable.printStackTrace();
        }
    }
}
