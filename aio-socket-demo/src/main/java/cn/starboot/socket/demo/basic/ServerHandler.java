package cn.starboot.socket.demo.basic;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringHandler;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.ChannelContext;

public class ServerHandler extends StringHandler {
	@Override
	public Packet handle(ChannelContext channelContext, StringPacket packet) {
		return packet;
	}
}
