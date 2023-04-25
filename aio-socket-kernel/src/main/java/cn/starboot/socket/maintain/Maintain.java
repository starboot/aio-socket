package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

public interface Maintain {

	boolean join(String id, ChannelContext context);

	boolean remove(String id, ChannelContext context);

	boolean removeAll(String id, ChannelContext context);

	<T> T get(String id, Class<T> t);
}
