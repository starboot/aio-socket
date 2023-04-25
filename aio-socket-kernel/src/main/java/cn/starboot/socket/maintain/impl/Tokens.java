package cn.starboot.socket.maintain.impl;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.maintain.AbstractMultiMaintain;
import cn.starboot.socket.maintain.MaintainEnum;

public final class Tokens extends AbstractMultiMaintain {

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

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.TOKEN;
	}
}
