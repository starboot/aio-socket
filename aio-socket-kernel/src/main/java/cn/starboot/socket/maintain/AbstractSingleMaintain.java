package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSingleMaintain extends AbstractMaintain {

	private final Map<String, ChannelContext> singleMaintainMap = new ConcurrentHashMap<>();

	public Map<String, ChannelContext> getSingleMaintainMap() {
		return singleMaintainMap;
	}
}
