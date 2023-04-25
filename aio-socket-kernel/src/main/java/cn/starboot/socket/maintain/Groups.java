/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.maintain;

import cn.starboot.socket.Packet;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.ChannelContextFilter;

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
    public final synchronized boolean join(String group, ChannelContext context) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            groupUnit = new GroupUnit();
            channelGroup.put(group, groupUnit);
        }
        return groupUnit.groupList.add(context);
    }

    /**
     * 从指定群组中移除用户
     * @param group    群组ID
     * @param context  被移除的ChannelContext
     */
    public final synchronized boolean remove(String group, ChannelContext context) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            return true;
        }
		boolean remove = groupUnit.groupList.remove(context);
		if (groupUnit.groupList.isEmpty()) {
            channelGroup.remove(group);
        }
		return remove;
    }

    /**
     * 从所有群组中移除用户
     *
     * @param context 被移除的ChannelContext
     */
    public final boolean removeUserFromAllGroup(ChannelContext context) {
    	boolean isSuccess = true;
        for (String group : channelGroup.keySet()) {
            isSuccess = isSuccess && remove(group, context);
        }
        return isSuccess;
    }

    /**
     * 群发
     *
     * @param group          群组ID
     * @param packet         消息包
     * @param channelContext 发送者channelContext
     */
    public void writeToGroup(String group, Packet packet, ChannelContext channelContext, ChannelContextFilter channelContextFilter, boolean isBlock) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            return;
        }
        if (channelContextFilter != null) {
			for (ChannelContext context : groupUnit.groupList) {
				if (!channelContextFilter.filter(context)) {
					send(context, packet, isBlock);
				}
			}
		} else {
			for (ChannelContext context : groupUnit.groupList) {
				send(context, packet, isBlock);
			}
		}
    }

    private static class GroupUnit {
        Set<ChannelContext> groupList = new HashSet<>();
    }

    private void send(ChannelContext channelContext, Packet packet, boolean isBlock) {
		if (isBlock) {
			Aio.bSend(channelContext, packet);
		} else {
			Aio.send(channelContext, packet);
		}
	}
}
