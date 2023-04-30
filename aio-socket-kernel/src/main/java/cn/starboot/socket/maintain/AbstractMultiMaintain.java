package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.utils.lock.SetWithLock;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMultiMaintain extends AbstractMaintain {

	/**
	 * 采用并发安全数据结构处理一对多中群组问题
	 */
	private final Map<String, SetWithLock<ChannelContext>> multiMaintainMap = new ConcurrentHashMap<>();

	protected Map<String, SetWithLock<ChannelContext>> getMultiMaintainMap() {
		return this.multiMaintainMap;
	}

	/**
	 * 将ChannelContext加入指定群组group
	 *
	 * @param id      组ID
	 * @param context 待加入的ChannelContext上下文信息
	 * @return        加入状态
	 */
	@Override
	public boolean join(String id, ChannelContext context) {
		SetWithLock<ChannelContext> channelContexts = getMultiMaintainMap().get(id);
		if (Objects.isNull(channelContexts)) {
			channelContexts = new SetWithLock<>(new HashSet<>());
		}
		return channelContexts.add(context);
	}

	/**
	 * 从指定群组中移除用户
	 *
	 * @param id      群组ID
	 * @param context 被移除的ChannelContext上下文信息
	 * @return        移除状态
	 */
	@Override
	public boolean remove(String id, ChannelContext context) {
		if (Objects.isNull(id)
				|| id.equals("")
				|| id.length() == 0
				|| Objects.isNull(context)) {
			return false;
		}
		boolean result;
		if (Objects.isNull(getMultiMaintainMap().get(id))
				&& !(getMultiMaintainMap().get(id).getObj().contains(context))) {
			return true;
		}else {
			result = getMultiMaintainMap().get(id).remove(context);
			if (getMultiMaintainMap().get(id).size() == 0) {
				getMultiMaintainMap().remove(id);
			}
		}
		return result;
	}

	/**
	 * 从所有群组中移除用户
	 *
	 * @param context 被移除的ChannelContext上下文信息
	 * @return        移除状态
	 */
	@Override
	public boolean removeAll(ChannelContext context) {
		boolean result = true;
		for (String groupId : getMultiMaintainMap().keySet()) {
			result = result && remove(groupId, context);
		}
		return result;
	}

	@Override
	public SetWithLock<ChannelContext> getSet(String id) {
		return Objects.isNull(getMultiMaintainMap().get(id)) ? null : getMultiMaintainMap().get(id);
	}
}
