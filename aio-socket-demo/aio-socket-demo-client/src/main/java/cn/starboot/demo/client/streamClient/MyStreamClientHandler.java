package cn.starboot.demo.client.streamClient;

import cn.starboot.demo.common.TestPacket;
import cn.starboot.socket.Packet;
import cn.starboot.socket.ProtocolEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.AIOUtil;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class MyStreamClientHandler implements AioHandler {

	private int maxLength;

	private Charset charsets;

	public MyStreamClientHandler() {
	}

	public MyStreamClientHandler(int maxLength) {
		this.maxLength = maxLength;
	}

	public MyStreamClientHandler(int maxLength, Charset charsets) {
		this(maxLength);
		this.charsets = charsets;
	}

	@Override
	public Packet handle(ChannelContext channelContext, Packet packet) {
		if (packet instanceof TestPacket) {
			return handle(channelContext, (TestPacket) packet);
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
		return charsets != null ? new TestPacket(new String(b, charsets)) : new TestPacket(new String(b));
	}

	@Override
	public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		WriteBuffer writeBuffer = channelContext.getWriteBuffer();
		TestPacket packet1 = (TestPacket) packet;

		int num = (int) (Math.random() * 10) + 1;
		writeBuffer.writeInt(packet1.getData().getBytes().length * num);
		while (num-- > 0) {
			writeBuffer.write(packet1.getData().getBytes());
		}
	}

	@Override
	public ProtocolEnum name() {
		return ProtocolEnum.PRIVATE_TCP;
	}

	public Packet handle(ChannelContext channelContext, TestPacket packet) {
//		System.out.println(packet.getData());
		return null;
	};
}
