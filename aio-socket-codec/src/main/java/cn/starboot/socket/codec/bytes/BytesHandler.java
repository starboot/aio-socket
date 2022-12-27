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
package cn.starboot.socket.codec.bytes;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.Packet;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.ProtocolEnum;

public abstract class BytesHandler implements AioHandler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof BytesPacket) {
            return handle(channelContext, (BytesPacket) packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
        return null;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {

    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.BYTES;
    }

    public abstract Packet handle(ChannelContext channelContext, BytesPacket packet);
}
