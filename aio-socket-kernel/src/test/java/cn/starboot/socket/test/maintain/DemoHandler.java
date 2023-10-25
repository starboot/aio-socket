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
package cn.starboot.socket.test.maintain;

import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.Packet;
import cn.starboot.socket.core.enums.ProtocolEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.core.exception.AioEncoderException;
import cn.starboot.socket.core.intf.AioHandler;
import cn.starboot.socket.test.core.DemoPacket;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;

import java.nio.ByteBuffer;

public class DemoHandler implements AioHandler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        return null;
    }

    @Override
    public Packet decode(MemoryUnit memoryUnit, ChannelContext channelContext) {
        ByteBuffer buffer = memoryUnit.buffer();
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int anInt = buffer.getInt();
        int anInt1 = buffer.getInt();
        int length = buffer.getInt();
        byte[] b = Aio.UtilApi.getBytesFromByteBuffer(memoryUnit, length, Integer.BYTES * 3, channelContext);
        if (b == null) {
            buffer.reset();
            return null;
        }
        DemoPacket demoPacket = new DemoPacket(new String(b));
        demoPacket.setFromId(String.valueOf(anInt));
        demoPacket.setToId(String.valueOf(anInt1));
        return demoPacket;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {
        if (packet instanceof DemoPacket) {
            DemoPacket demoPacket = (DemoPacket) packet;
            // 自定义协议
            WriteBuffer writeBuffer = channelContext.getWriteBuffer();
            try {
                if (demoPacket.getFromId() != null) {
                    writeBuffer.writeInt(Integer.parseInt(demoPacket.getFromId()));
                }
                if (demoPacket.getToId() != null) {
                    writeBuffer.writeInt(Integer.parseInt(demoPacket.getToId()));
                }
                writeBuffer.writeInt(demoPacket.getData().getBytes().length);
                writeBuffer.write(demoPacket.getData().getBytes());
            } catch (AioEncoderException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ProtocolEnum name() {
        return null;
    }
}
