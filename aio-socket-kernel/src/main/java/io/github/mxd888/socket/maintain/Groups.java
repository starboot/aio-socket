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
package io.github.mxd888.socket.maintain;

import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.TCPChannelContext;

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
    public final synchronized void join(String group, TCPChannelContext context) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            groupUnit = new GroupUnit();
            channelGroup.put(group, groupUnit);
        }
        groupUnit.groupList.add(context);
    }

    public final synchronized void remove(String group, TCPChannelContext context) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            return;
        }
        groupUnit.groupList.remove(context);
        if (groupUnit.groupList.isEmpty()) {
            channelGroup.remove(group);
        }
    }

    public final void remove(TCPChannelContext context) {
        for (String group : channelGroup.keySet()) {
            remove(group, context);
        }
    }

    public void writeToGroup(String group, VirtualBuffer buffer, TCPChannelContext channelContext) {
        GroupUnit groupUnit = channelGroup.get(group);
        if (groupUnit == null) {
            return;
        }
        if (channelContext == null) {
            for (TCPChannelContext context : groupUnit.groupList) {
//                Aio.send(context, buffer);
            }
            return;
        }
        for (TCPChannelContext context : groupUnit.groupList) {
            if (channelContext != context) {
//                Aio.send(context, buffer);
            }
        }
    }

    private static class GroupUnit {
        Set<TCPChannelContext> groupList = new HashSet<>();
    }
}
