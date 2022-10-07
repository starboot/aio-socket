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
