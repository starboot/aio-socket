package cn.starboot.socket.test.core;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.ChannelContextFilter;

public class myChannelContextFilter implements ChannelContextFilter {

	@Override
	public boolean filter(ChannelContext channelContext) {
		return false;
	}
}
