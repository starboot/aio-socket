package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.utils.lock.SetWithLock;

public abstract class AbstractMaintain implements Maintain{

	public abstract MaintainEnum getName();

	public SetWithLock<ChannelContext> getSet(String id) {
		return null;
	}

	public ChannelContext getChannelContext(String id) {
		return null;
	}

}
