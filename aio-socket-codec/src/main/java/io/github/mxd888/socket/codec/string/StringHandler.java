package io.github.mxd888.socket.codec.string;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.AIOUtil;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class StringHandler implements AioHandler<String> {

    @Override
    public abstract Packet<String> handle(ChannelContext channelContext, Packet<String> packet);

    @Override
    public Packet<String> decode(MemoryUnit memoryUnit, ChannelContext channelContext) throws AioDecoderException {
        ByteBuffer buffer = memoryUnit.buffer();
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int length = buffer.getInt();
        byte[] b = AIOUtil.getBytesFromByteBuffer(length, memoryUnit, channelContext);
        if (b == null) {
            buffer.reset();
            return null;
        }
        // 不使用UTF_8性能会提升8%
        return new Packet<>(new String(b, StandardCharsets.UTF_8));
    }

    @Override
    public void encode(Packet<String> packet, ChannelContext channelContext) {
        WriteBuffer writeBuffer = channelContext.getWriteBuffer();
        try {
            writeBuffer.writeInt(packet.getData().getBytes().length);
            writeBuffer.write(packet.getData().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
