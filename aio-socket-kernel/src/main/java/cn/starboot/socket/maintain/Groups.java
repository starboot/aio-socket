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

    /**
     * 从指定群组中移除用户
     * @param group    群组ID
     * @param context  被移除的ChannelContext
     */
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

    /**
     * 从所有群组中移除用户
     *
     * @param context 被移除的ChannelContext
     */
    public final void remove(ChannelContext context) {
        for (String group : channelGroup.keySet()) {
            remove(group, context);
        }
    }

    /**
     * 群发
     *
     * @param group          群组ID
     * @param packet         消息包
     * @param channelContext 发送者channelContext
     */
    public void writeToGroup(String group, Packet packet, ChannelContext channelContext) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            return;
        }
        if (channelContext == null) {
            for (ChannelContext context : groupUnit.groupList) {
                Aio.send(context, packet);
            }
            return;
        }
        for (ChannelContext context : groupUnit.groupList) {
            if (channelContext != context) {
                Aio.send(context, packet);
            }
        }
    }

    private static class GroupUnit {
        Set<ChannelContext> groupList = new HashSet<>();
    }
}
