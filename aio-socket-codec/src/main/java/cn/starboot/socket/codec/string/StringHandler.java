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
package cn.starboot.socket.codec.string;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.AIOUtil;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.Packet;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.enums.ProtocolEnum;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class StringHandler implements AioHandler {

    private int maxLength;

    private Charset charsets;

    public StringHandler() {
    }

    public StringHandler(int maxLength) {
        this.maxLength = maxLength;
    }

    public StringHandler(int maxLength, Charset charsets) {
        this(maxLength);
        this.charsets = charsets;
    }

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof StringPacket) {
            return handle(channelContext, (StringPacket) packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit memoryUnit, ChannelContext channelContext) throws AioDecoderException {
        ByteBuffer buffer = memoryUnit.buffer();
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int length = buffer.getInt();
        if (maxLength > 0 && length > maxLength) {
            buffer.reset();
            return null;
        }
        byte[] b = AIOUtil.getBytesFromByteBuffer(memoryUnit, length, Integer.BYTES, channelContext);
        if (b == null) {
            buffer.reset();
            return null;
        }
        // 不使用UTF_8性能会提升8%
		Packet packet = new Packet();
		packet.setTestData(b);
		return packet;
//		return charsets != null ? new StringPacket(new String(b, charsets)) : new StringPacket(new String(b));
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
        WriteBuffer writeBuffer = channelContext.getWriteBuffer();
		byte[] testData = packet.getTestData();
		writeBuffer.writeInt(testData.length);
		writeBuffer.write(testData);
//        if (packet instanceof StringPacket) {
//			StringPacket packet1 = (StringPacket) packet;
//			writeBuffer.writeInt(packet1.getData().getBytes().length);
//			writeBuffer.write(packet1.getData().getBytes());
//		}else {
//			throw new AioEncoderException("String Protocol encode failed because of the Packet is not StringPacket.");
//		}

    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.STRING;
    }

    public abstract Packet handle(ChannelContext channelContext, StringPacket packet);
}
