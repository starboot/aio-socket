package io.github.mxd888.socket.intf;

import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.ChannelContext;

public interface StateMachineEvent {

    /**
     * 状态机事件,当枚举事件发生时由框架触发该方法
     *
     * @param channelContext                            本次触发状态机的ChannelContext对象
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
