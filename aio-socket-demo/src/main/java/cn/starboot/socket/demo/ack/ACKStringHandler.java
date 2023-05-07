package cn.starboot.socket.demo.ack;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringHandler;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import java.nio.ByteBuffer;

public abstract class ACKStringHandler extends StringHandler {

	private static final byte reqMark = (byte) 0x00;

	private static final byte respMark = (byte) 0xff;

	@Override
	public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		super.encode(packet, channelContext);
		WriteBuffer writeBuffer = channelContext.getWriteBuffer();
		// 写同步位
		if (packet.getReq() != null && !packet.getReq().equals("")) {
			byte[] bytes = packet.getReq().getBytes();
			writeBuffer.write(reqMark);
			writeBuffer.writeInt(bytes.length);
			writeBuffer.write(bytes);
		}
		if (packet.getResp() != null && !packet.getResp().equals("")) {
			byte[] bytes = packet.getResp().getBytes();
			writeBuffer.write(respMark);
			writeBuffer.writeInt(bytes.length);
			writeBuffer.write(bytes);
		}

	}

	@Override
	public Packet decode(MemoryUnit memoryUnit, ChannelContext channelContext) throws AioDecoderException {
		Packet decode = super.decode(memoryUnit, channelContext);
		ByteBuffer buffer = memoryUnit.buffer();
		buffer.mark();
		if (buffer.remaining() > 0 && buffer.get() == reqMark) {
			setReqMark(buffer, decode);
		}else {
			buffer.reset();
		}
		buffer.mark();
		if (buffer.remaining() > 0 && buffer.get() == respMark) {
			setRespMark(buffer, decode);
		}else {
			buffer.reset();
		}
		return decode;
	}

	private void setReqMark(ByteBuffer buffer, Packet decode) {
		int anInt = buffer.getInt();
		byte[] bytes = new byte[anInt];
		buffer.get(bytes);
		decode.setReq(new String(bytes));
	}

	private void setRespMark(ByteBuffer buffer, Packet decode) {
		int anInt = buffer.getInt();
		byte[] bytes = new byte[anInt];
		buffer.get(bytes);
		decode.setResp(new String(bytes));
	}
}
