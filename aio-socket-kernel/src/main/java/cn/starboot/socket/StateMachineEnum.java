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
package cn.starboot.socket;

/**
 * 列举了当前aio-socket Channel通道所关注的各类状态枚举。
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum StateMachineEnum {

    /**
     * 连接已建立并构建Channel对象
     */
    NEW_CHANNEL,

    /**
     * 读通道已被关闭。
     */
    INPUT_SHUTDOWN,

    /**
     * 业务处理异常。
     */
    PROCESS_EXCEPTION,

    /**
     * 协议解码异常。
     */
    DECODE_EXCEPTION,

    /**
     * 读操作异常。
     */
    INPUT_EXCEPTION,

    /**
     * 写操作异常。
     */
    OUTPUT_EXCEPTION,

    /**
     * 会话正在关闭中。
     */
    CHANNEL_CLOSING,

    /**
     * 会话关闭成功。
     */
    CHANNEL_CLOSED,

    /**
     * 拒绝接受连接,仅Server端有效
     */
    REJECT_ACCEPT

}
