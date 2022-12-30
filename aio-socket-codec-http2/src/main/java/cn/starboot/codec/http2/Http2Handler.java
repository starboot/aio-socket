package cn.starboot.codec.http2;

import cn.starboot.socket.Packet;
import cn.starboot.socket.ProtocolEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.exception.AioDecoderException;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;

public class Http2Handler implements AioHandler {
	@Override
	public Packet handle(ChannelContext channelContext, Packet packet) {
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
		return ProtocolEnum.HTTP2;
	}
}