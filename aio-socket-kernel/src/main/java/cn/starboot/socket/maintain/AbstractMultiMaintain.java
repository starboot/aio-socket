package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMultiMaintain extends AbstractMaintain {

	private final Map<String, Set<ChannelContext>> multiMaintainMap = new ConcurrentHashMap<>();

	public Map<String, Set<ChannelContext>> getMultiMaintainMap() {
		return multiMaintainMap;
	}
}
