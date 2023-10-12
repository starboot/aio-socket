package cn.starboot.socket.demo.basic;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringHandler;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.ChannelContext;

import java.io.IOException;

public class ServerHandler extends StringHandler {
	@Override
	public Packet handle(ChannelContext channelContext, StringPacket packet) {

		try {
			System.out.println("收到来自客户端" + channelContext.getRemoteAddress() + "的消息:" + packet.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 将消息回发给客户端
		return packet;
	}
}
