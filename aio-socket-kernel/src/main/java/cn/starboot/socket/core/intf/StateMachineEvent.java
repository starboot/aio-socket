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
package cn.starboot.socket.core.intf;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.enums.StateMachineEnum;

public interface StateMachineEvent {

    /**
     * 状态机事件,当枚举事件发生时由框架触发该方法
     *
     * @param channelContext                            本次触发状态机的ChannelContext对象
     * @param stateMachineEnum                          状态枚举
     * @param throwable                                 异常对象，如果存在的话
     * @see StateMachineEnum    状态机枚举
     */
    default void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        if (stateMachineEnum == StateMachineEnum.DECODE_EXCEPTION
				|| stateMachineEnum == StateMachineEnum.PROCESS_EXCEPTION
		) {
            throwable.printStackTrace();
        }
    }
}
