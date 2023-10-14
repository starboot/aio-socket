package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.enums.MaintainEnum;
import cn.starboot.socket.utils.concurrent.collection.ConcurrentWithSet;

public abstract class AbstractMaintain implements Maintain{

	public abstract MaintainEnum getName();

	public ConcurrentWithSet<ChannelContext> getSet(String id) {
		return null;
	}

	public ChannelContext getChannelContext(String id) {
		return null;
	}

}
