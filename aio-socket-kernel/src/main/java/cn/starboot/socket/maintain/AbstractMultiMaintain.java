package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMultiMaintain extends AbstractMaintain {

	private final Map<String, Set<ChannelContext>> multiMaintainMap = new ConcurrentHashMap<>();

	protected Map<String, Set<ChannelContext>> getMultiMaintainMap() {
		return multiMaintainMap;
	}

	@Override
	public boolean join(String id, ChannelContext context) {
		Set<ChannelContext> channelContexts = getMultiMaintainMap().get(id);
		if (Objects.isNull(channelContexts)) {
			channelContexts = new HashSet<>();
		}
		return channelContexts.contains(context) || channelContexts.add(context);
	}

	@Override
	public boolean remove(String id, ChannelContext context) {
		boolean result;
		if (Objects.isNull(getMultiMaintainMap().get(id))) {
			return true;
		}else {
			result = getMultiMaintainMap().get(id).remove(context);
			if (getMultiMaintainMap().get(id).isEmpty()) {
				getMultiMaintainMap().remove(id);
			}
		}
		return result;
	}

	@Override
	public boolean removeAll(String id, ChannelContext context) {
		boolean result = true;
		for (String groupId : getMultiMaintainMap().keySet()) {
			result = result && remove(groupId, context);
		}
		return result;
	}

	@Override
	public <T> T get(String id, Class<T> t) {
		return Objects.isNull(getMultiMaintainMap().get(id)) ? null : (T) getMultiMaintainMap().get(id);
	}
}
