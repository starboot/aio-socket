package cn.starboot.socket.demo.batch.client;

import cn.starboot.socket.core.Packet;
import cn.starboot.socket.codec.bytes.BytesHandler;
import cn.starboot.socket.codec.bytes.BytesPacket;
import cn.starboot.socket.core.ChannelContext;

public class ProcessorClientHandler extends BytesHandler {

	@Override
	public Packet handle(ChannelContext channelContext, BytesPacket packet) {
		return null;
	}
}
