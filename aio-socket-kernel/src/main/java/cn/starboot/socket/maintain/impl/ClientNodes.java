package cn.starboot.socket.maintain.impl;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.maintain.AbstractSingleMaintain;
import cn.starboot.socket.maintain.MaintainEnum;

/**
 * 一对一
 */
public class ClientNodes extends AbstractSingleMaintain {
	@Override
	public MaintainEnum getName() {
		return null;
	}

	@Override
	public boolean join(String id, ChannelContext context) {
		return false;
	}

	@Override
	public boolean remove(String id, ChannelContext context) {
		return false;
	}

	@Override
	public boolean removeAll(String id, ChannelContext context) {
		return false;
	}

	@Override
	public <T> T get(String id, Class<T> t) {
		return null;
	}
}
