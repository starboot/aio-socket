package cn.starboot.socket.demo.ack;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.ChannelContext;

public class ClientHandler extends ACKStringHandler {

	@Override
	public Packet handle(ChannelContext channelContext, StringPacket packet) {
		System.out.println("收到消息：" + packet.getData() + "-ack: " + packet.getResp());
		return null;
	}
}
