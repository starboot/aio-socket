package cn.starboot.socket.demo.ack;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringHandler;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.exception.AioEncoderException;

public class ClientHandler extends StringHandler {

	@Override
	public Packet handle(ChannelContext channelContext, StringPacket packet) {
		System.out.println("收到消息：" + packet.getData());
		return null;
	}

	@Override
	public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		super.encode(packet, channelContext);
		// 写同步位
	}
}
