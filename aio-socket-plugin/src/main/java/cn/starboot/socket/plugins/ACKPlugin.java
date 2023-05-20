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
package cn.starboot.socket.plugins;

import cn.starboot.socket.Packet;
import cn.starboot.socket.core.Aio;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.utils.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by DELL(mxd) on 2022/7/28 17:48
 */
public class ACKPlugin extends AbstractPlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(ACKPlugin.class);

	private final Map<ChannelContext, Set<Packet>> idToPacket = new HashMap<>();

	private final Semaphore semaphore = new Semaphore(1);

	private final long timeout;

	private final long period;

	public ACKPlugin(int timeout, int period, TimeUnit timeUnit) {
		if (timeout <= 0) {
			throw new IllegalArgumentException("timeout should bigger than zero");
		}
		this.timeout = timeUnit.toMillis(timeout);
		this.period =  timeUnit.toMillis(period);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("aio-socket version: " + AioConfig.VERSION + "; server kernel's ACK plugin added successfully");
		}
	}

	@Override
	public boolean beforeProcess(ChannelContext channelContext, Packet packet) {
		// 解码后得到的数据进行处理ACK确认
		String resp = packet.getResp();
		if (resp != null && resp.length() != 0) {
			Set<Packet> packets = idToPacket.get(channelContext);
			if (Objects.nonNull(packets) && packets.size() > 0) {
				packets.remove(packet);
				if (packets.size() == 0) {
					idToPacket.remove(channelContext);
				}
			} else
				idToPacket.remove(channelContext);
		}
		return true;
	}

	@Override
	public void beforeEncode(Packet packet, ChannelContext channelContext) {
		// 编码前对数据进行ACK码计时
		String req = packet.getReq();
		if (req != null && req.length() != 0) {
			packet.setLatestTime(System.currentTimeMillis());
			Set<Packet> packets = idToPacket.get(channelContext);
			if (Objects.isNull(packets)) {
				packets = new HashSet<>();
			}
			packets.add(packet);
			idToPacket.put(channelContext, packets);
			if (semaphore.tryAcquire()) {
				registerACK();
			}
		}
	}

	private void registerACK() {
		TimerService.getInstance().schedule(new TimerTask() {
			@Override
			public void run() {
				if (idToPacket.size() == 0) {
					semaphore.release();
					return;
				}
				idToPacket.forEach((channelContext, packets) -> {
					if (channelContext.isInvalid()) {
						idToPacket.remove(channelContext);
					} else {
						packets.forEach(packet -> {
							long lastTime = packet.getLatestTime();
							long current = System.currentTimeMillis();
							//超时未收到消息，重新发送
							if (timeout > 0 && (current - lastTime) > timeout) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("ChannelContextId {} -> messageId:{} has timeout ,retry to send...", channelContext.getId(), packet.getReq());
								}
								Aio.send(channelContext, packet);
							}
						});
					}
				});
				registerACK();
			}
		}, this.period, TimeUnit.MILLISECONDS);
	}
}
