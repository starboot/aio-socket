package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

interface Maintain {

	boolean join(String id, ChannelContext context);

	boolean remove(String id, ChannelContext context);

	boolean removeAll(ChannelContext context);
}
