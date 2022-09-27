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
package io.github.mxd888.demo.common;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.TCPChannelContext;
import io.github.mxd888.socket.intf.AioHandler;

import java.nio.ByteBuffer;

public class Handler implements AioHandler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        return null;
    }

    @Override
    public Packet decode(VirtualBuffer virtualBuffer, ChannelContext channelContext) {
        ByteBuffer buffer = virtualBuffer.buffer();
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int length = buffer.getInt();
        if (length > buffer.remaining()) {
            buffer.reset();
            return null;
        }
        byte[] b = new byte[length];
        buffer.get(b);
        return new DemoPacket(new String(b));
    }

    @Override
    public VirtualBuffer encode(Packet packet, ChannelContext channelContext, VirtualBuffer writeBuffer) {
        if (packet instanceof DemoPacket) {
            DemoPacket demoPacket = (DemoPacket) packet;
            // 自定义协议
            VirtualBuffer virtualBuffer = channelContext.getVirtualBuffer(20);
            ByteBuffer buffer = virtualBuffer.buffer();
            buffer.putInt(demoPacket.getData().getBytes().length);
            buffer.put(demoPacket.getData().getBytes());
            return writeBuffer;
        }
        return null;
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        switch (stateMachineEnum){
            case DECODE_EXCEPTION:
                System.out.println("解码异常");
                throwable.printStackTrace();
                break;
            case PROCESS_EXCEPTION:
                System.out.println("处理异常");
                break;
            case INPUT_SHUTDOWN:
                System.out.println("输入流已关闭");
                break;
            case REJECT_ACCEPT:
                System.out.println("超过负载，拒绝连接");
                break;
        }
    }
}
