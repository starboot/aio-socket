package cn.starboot.socket.demo.basic;

import cn.starboot.socket.core.Packet;
import cn.starboot.socket.codec.string.StringHandler;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.ChannelContext;

public class ClientHandler extends StringHandler {
	@Override
	public Packet handle(ChannelContext channelContext, StringPacket packet) {
		System.out.println("收到来自服务器的消息：" + packet.getData());
		return null;
	}
}
