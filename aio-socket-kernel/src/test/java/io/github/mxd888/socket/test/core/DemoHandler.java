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
package io.github.mxd888.socket.test.core;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.AIOUtil;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DemoHandler extends AioHandler {

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
//        int length = buffer.getInt();
        byte[] b = AIOUtil.getBytesFromByteBuffer(buffer.remaining(), memoryUnit, channelContext);
        if (b == null) {
            buffer.reset();
            return null;
        }
        return new DemoPacket(new String(b, StandardCharsets.UTF_8));
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {
        if (packet instanceof DemoPacket) {
            DemoPacket demoPacket = (DemoPacket) packet;
            // 自定义协议
            WriteBuffer writeBuffer = channelContext.getWriteBuffer();
            try {
                writeBuffer.writeInt(demoPacket.getData().getBytes().length);
                writeBuffer.write(demoPacket.getData().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ProtocolEnum name() {
        return null;
    }
}
