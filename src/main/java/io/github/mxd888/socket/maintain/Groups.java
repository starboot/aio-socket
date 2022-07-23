package io.github.mxd888.socket.maintain;

import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 群组业务逻辑类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Groups {

    private final Map<String, GroupUnit> channelGroup = new ConcurrentHashMap<>();

    /**
     * 将ChannelContext加入群组group
     *
     * @param group 群组ID
     * @param context 用户上下文
     */
    public final synchronized void join(String group, ChannelContext context) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            groupUnit = new GroupUnit();
            channelGroup.put(group, groupUnit);
        }
        groupUnit.groupList.add(context);
    }

    public final synchronized void remove(String group, ChannelContext context) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            return;
        }
        groupUnit.groupList.remove(context);
        if (groupUnit.groupList.isEmpty()) {
            channelGroup.remove(group);
        }
    }

    public final void remove(ChannelContext context) {
        for (String group : channelGroup.keySet()) {
            remove(group, context);
        }
    }

    public void writeToGroup(String group, VirtualBuffer buffer, ChannelContext channelContext) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            return;
        }
        if (channelContext == null) {
            for (ChannelContext context : groupUnit.groupList) {
                Aio.send(context, buffer);
            }
            return;
        }
        for (ChannelContext context : groupUnit.groupList) {
            if (channelContext != context) {
                Aio.send(context, buffer);
            }
        }
    }

    private static class GroupUnit {
        Set<ChannelContext> groupList = new HashSet<>();
    }
}
