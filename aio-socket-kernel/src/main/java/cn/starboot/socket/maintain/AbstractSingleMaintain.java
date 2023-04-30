package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSingleMaintain extends AbstractMaintain {

	private final Map<String, ChannelContext> singleMaintainMap = new ConcurrentHashMap<>();

	protected Map<String, ChannelContext> getSingleMaintainMap() {
		return singleMaintainMap;
	}

	/**
	 * 一对一加入
	 * 不存在 = 加入成功
	 * 存在 && 并且一样 = 加入成功
	 * 存在 && 不一样 = 加入失败
	 *
	 * @param id      一对一ID
	 * @param context 通道上下文
	 * @return 加入结果
	 */
	@Override
	public boolean join(String id, ChannelContext context) {
		if (Objects.isNull(getSingleMaintainMap().get(id))) {
			return Objects.nonNull(getSingleMaintainMap().put(id, context));
		} else {
			return Objects.equals(getSingleMaintainMap().get(id), context);
		}
	}

	/**
	 * 一对一删除
	 * 不存在 = 删除成功
	 * 存在 && 且相等 = 进行删除
	 * 存在 && 不相等 = 不进行删除
	 *
	 * @param id      一对一ID
	 * @param context 通道上下文
	 * @return 加入结果
	 */
	@Override
	public boolean remove(String id, ChannelContext context) {
		if (Objects.isNull(id) || id.equals("") || id.length() == 0) {
			if (Objects.isNull(context)) return false;
			AtomicBoolean r = new AtomicBoolean(false);
			getSingleMaintainMap().forEach((s, channelContext) -> {
				if (Objects.equals(channelContext, context)) {
					r.set(!Objects.isNull(s) && !s.equals("") && s.length() != 0 && remove(s, context));
				}
			});
			return r.get();
		}
		ChannelContext singleMaintainMapChannelContext = getSingleMaintainMap().get(id);
		if (Objects.isNull(singleMaintainMapChannelContext)) {
			return true;
		}
		if (Objects.isNull(context)) {
			return Objects.nonNull(getSingleMaintainMap().remove(id));
		}
		if (Objects.equals(singleMaintainMapChannelContext, context)) {
			return Objects.nonNull(getSingleMaintainMap().remove(id));
		} else return false;
	}

	@Override
	public boolean removeAll(ChannelContext context) {
		throw new UnsupportedOperationException("不支持此操作");
	}

	@Override
	public ChannelContext getChannelContext(String id) {
		return Objects.isNull(getSingleMaintainMap().get(id)) ? null : getSingleMaintainMap().get(id);
	}
}
