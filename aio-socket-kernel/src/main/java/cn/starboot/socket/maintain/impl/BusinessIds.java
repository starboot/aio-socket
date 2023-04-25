package cn.starboot.socket.maintain.impl;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.maintain.AbstractSingleMaintain;
import cn.starboot.socket.maintain.MaintainEnum;

/**
 * 1-1
 */
public class BusinessIds extends AbstractSingleMaintain {
	@Override
	public MaintainEnum getName() {
		return MaintainEnum.BUSINESS_ID;
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