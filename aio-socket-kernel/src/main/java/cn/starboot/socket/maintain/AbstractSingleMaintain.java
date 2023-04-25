package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
		ChannelContext singleMaintainMapChannelContext = getSingleMaintainMap().get(id);
		if (Objects.nonNull(singleMaintainMapChannelContext)
				&& Objects.equals(singleMaintainMapChannelContext, context)) {
			return Objects.nonNull(getSingleMaintainMap().remove(id));
		} else if (Objects.nonNull(singleMaintainMapChannelContext)
				&& !Objects.equals(singleMaintainMapChannelContext, context)) {
			return false;
		} else return Objects.isNull(singleMaintainMapChannelContext);
	}

	@Override
	public boolean removeAll(String id, ChannelContext context) {
		return remove(id, context);
	}

	@Override
	public <T> T get(String id, Class<T> t) {
		ChannelContext singleMaintainMapChannelContext = getSingleMaintainMap().get(id);
		return Objects.isNull(singleMaintainMapChannelContext) ? null : (T) singleMaintainMapChannelContext;
	}
}
