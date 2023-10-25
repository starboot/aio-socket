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
package cn.starboot.socket.codec.protobuf;

import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.core.exception.AioEncoderException;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.enums.ProtocolEnum;
import cn.starboot.socket.core.exception.AioDecoderException;

import java.nio.ByteBuffer;

public abstract class ProtoBufHandler implements AioHandler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof ProtoBufPacket) {
            return handle(channelContext, (ProtoBufPacket) packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
		ByteBuffer buffer = readBuffer.buffer();
		int remaining = buffer.remaining();
		if (remaining < Integer.BYTES) {
			return null;
		}
		buffer.mark();
		int length = buffer.getInt();
		byte[] b = Aio.UtilApi.getBytesFromByteBuffer(readBuffer, length, Integer.BYTES, channelContext);
		if (b == null) {
			buffer.reset();
			return null;
		}
        return new ProtoBufPacket(b);
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		WriteBuffer writeBuffer = channelContext.getWriteBuffer();
		if (packet instanceof ProtoBufPacket) {
			ProtoBufPacket packet1 = (ProtoBufPacket) packet;
			writeBuffer.writeInt(packet1.getLength());
			writeBuffer.write(packet1.getData());
		}else {
			throw new AioEncoderException("ProtoBuf Protocol encode failed because of the Packet is not ProtoBufPacket.");
		}

    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.PROTOBUF;
    }

    public abstract Packet handle(ChannelContext channelContext, ProtoBufPacket packet);
}
