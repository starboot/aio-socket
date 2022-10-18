package io.github.mxd888.socket.codec.string;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.codec.IProtocol;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.AIOUtil;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class StringHandler implements AioHandler, IProtocol {

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
        byte[] b = AIOUtil.getBytesFromByteBuffer(length, memoryUnit, channelContext);
        if (b == null) {
            buffer.reset();
            return null;
        }
        // 不使用UTF_8性能会提升8%
        return charsets != null ? new StringPacket(new String(b, charsets)) : new StringPacket(new String(b));
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {
        WriteBuffer writeBuffer = channelContext.getWriteBuffer();
        try {
            StringPacket packet1 = (StringPacket) packet;
            writeBuffer.writeInt(packet1.getData().getBytes().length);
            writeBuffer.write(packet1.getData().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String name() {
        return "StringProtocol";
    }

    public abstract Packet handle(ChannelContext channelContext, StringPacket packet);
}
