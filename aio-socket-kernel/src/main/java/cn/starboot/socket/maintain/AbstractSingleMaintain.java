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

	@Override
	public boolean join(String id, ChannelContext context) {
		return Objects.nonNull(getSingleMaintainMap().put(id, context));
	}

	@Override
	public boolean remove(String id, ChannelContext context) {
		ChannelContext singleMaintainMapChannelContext = getSingleMaintainMap().get(id);
		if (Objects.nonNull(singleMaintainMapChannelContext)
				&& Objects.equals(singleMaintainMapChannelContext,context)) {
			return Objects.nonNull(getSingleMaintainMap().remove(id));
		}else return Objects.isNull(singleMaintainMapChannelContext);
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
