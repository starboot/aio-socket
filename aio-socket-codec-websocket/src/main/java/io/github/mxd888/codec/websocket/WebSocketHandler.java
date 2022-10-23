package io.github.mxd888.codec.websocket;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

public class WebSocketHandler extends AioHandler {

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
		return ProtocolEnum.WEBSOCKET;
	}
}
