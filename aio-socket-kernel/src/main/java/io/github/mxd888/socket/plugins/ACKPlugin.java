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
package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.QuickTimerTask;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by DELL(mxd) on 2022/7/28 17:48
 */
public class ACKPlugin extends AbstractPlugin{

    private static final TimeoutCallback DEFAULT_TIMEOUT_CALLBACK = (packet, lastTime) -> System.out.println(packet.getReq() + " : has timeout");

    private final Map<String, Packet> idToPacket = new HashMap<>();

    private final Map<String, Long> timePacket = new HashMap<>();

    private final long timeout;

    private final TimeoutCallback timeoutCallback;

    public ACKPlugin(int timeout, TimeUnit timeUnit) {
        this(timeout, timeUnit, DEFAULT_TIMEOUT_CALLBACK);
    }

    public ACKPlugin(int timeout, TimeUnit timeUnit, TimeoutCallback timeoutCallback) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout should bigger than zero");
        }
        this.timeout = timeUnit.toMillis(timeout);
        this.timeoutCallback = timeoutCallback;
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's ACK plugin added successfully");
    }

    @Override
    public void afterDecode(Packet packet, ChannelContext channelContext) {
        // 解码后得到的数据进行处理ACK确认
        String resp = packet.getResp();
        if (resp != null && resp.length() != 0) {
            idToPacket.remove(resp);
        }
    }

    @Override
    public void beforeEncode(Packet packet, ChannelContext channelContext) {
        // 编码前对数据进行ACK码计时
        String req = packet.getReq();
        if (req != null && req.length() != 0) {
            idToPacket.put(req, packet);
            registerACK(req, packet);
        }
    }

    private void registerACK(final String key, Packet packet) {
        QuickTimerTask.SCHEDULED_EXECUTOR_SERVICE.schedule(new TimerTask() {
            @Override
            public void run() {
                if (idToPacket.get(key) == null) {
                    return;
                }
                Long lastTime = timePacket.get(key);
                if (lastTime == null) {
                    lastTime = System.currentTimeMillis();
                    timePacket.put(key, lastTime);
                }
                long current = System.currentTimeMillis();
                //超时未收到消息，关闭连接
                if (timeout > 0 && (current - lastTime) > timeout) {
                    timeoutCallback.callback(packet, lastTime);
                }
                registerACK(key, packet);
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }

    public interface TimeoutCallback {

        void callback(Packet packet, long lastTime);
    }
}
