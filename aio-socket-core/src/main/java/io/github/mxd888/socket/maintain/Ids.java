package io.github.mxd888.socket.maintain;

import io.github.mxd888.socket.core.ChannelContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ID业务逻辑类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Ids {

    private final Map<String, ChannelContext> channelIds = new ConcurrentHashMap<>();

    /**
     * 将ChannelContext加入channelIds
     *
     * @param userId 用户ID
     * @param context 用户上下文
     */
    public final synchronized void join(String userId, ChannelContext context) {
        channelIds.put(userId, context);
    }

    public final void remove(String userId) {
        ChannelContext context = channelIds.get(userId);
        if (context == null) {
            return;
        }
        channelIds.remove(userId);
    }

    public ChannelContext get(String userId) {
        return channelIds.get(userId);
    }

}
