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
package cn.starboot.socket.core;

import cn.starboot.socket.Packet;
import cn.starboot.socket.core.config.AioConfig;
import cn.starboot.socket.maintain.MaintainEnum;
import cn.starboot.socket.utils.lock.SetWithLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 基础常用API
 * 绑定ID、绑定群组、单发、群发、移出群、关闭连接
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Aio {

	private static final Logger LOGGER = LoggerFactory.getLogger(Aio.class);

	public static boolean bindBsId(String bsId, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.Bs_ID).join(bsId, channelContext);
	}

	public static boolean bindCliNode(String cliNode, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.CLIENT_NODE_ID).join(cliNode, channelContext);
	}

	public static boolean bindCluId(String CluId, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.CLU_ID).join(CluId, channelContext);
	}

	public static boolean bindGroup(String groupId, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.GROUP_ID).join(groupId, channelContext);
	}

	public static boolean bindGroup(String groupId, String userId, AioConfig aioConfig) {
		return false;
//		return aioConfig.getMaintainManager().getCommand(MaintainEnum.GROUP_ID).join(groupId, channelContext);
	}

	public static boolean bindId(String id, ChannelContext channelContext) {
		channelContext.setId(id);
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.ID).join(id, channelContext);
	}

	public static boolean bindIp(String ip, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.IP).join(ip, channelContext);
	}

	public static boolean bindToken(String token, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.TOKEN).join(token, channelContext);
	}

	public static boolean bindUser(String user, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.USER).join(user, channelContext);
	}

	public static boolean bSend(ChannelContext channelContext, Packet packet) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return send0(channelContext, packet, true);
	}

	/**
	 * 阻塞发送到指定 IP + port
	 *
	 * @param ip
	 * @param port
	 * @param aioConfig
	 * @param packet
	 */
	public static void bSend(String ip, int port, AioConfig aioConfig, Packet packet) {
		//
	}

	public static void bSendToAll(AioConfig aioConfig, Packet packet, ChannelContextFilter channelContextFilter) {

	}

	public static void bSendToBsId() {

	}

	public static boolean bSendToGroup(String groupId, Packet packet, AioConfig aioConfig) {
		return bSendToGroup(groupId, packet, aioConfig, null);
	}

	public static boolean bSendToGroup(String groupId, Packet packet, AioConfig aioConfig, ChannelContextFilter channelContextFilter) {
		return true;
	}

	/**
	 * 关闭某个连接
	 *
	 * @param channelContext 通道上下文
	 */
	public static void close(ChannelContext channelContext) {
		removeUserFromAllGroup(channelContext);
		channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.ID).remove(channelContext.getId(), channelContext);
		channelContext.close();
	}

	// G

	public static void getAll(AioConfig aioConfig) {

	}

	public static void getByBsId(AioConfig aioConfig, String bsId) {

	}


	public static ChannelContext getChannelContextById(String channelContextId, AioConfig config) {
		return config.getMaintainManager().getCommand(MaintainEnum.ID).get(channelContextId, ChannelContext.class);
	}

	public static void getChannelContextByBsId(AioConfig aioConfig, String bsId) {

	}

	public static void groupCount(AioConfig aioConfig, String groupId) {

	}

	// I

	public static void isInGroup(String groupId, ChannelContext channelContext) {

	}

	// R

	public static void remove(AioConfig aioConfig, String clientIp, Integer clientPort, Throwable throwable, String remark) {

	}

	public static boolean removeUserFromAllGroup(ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.GROUP_ID).removeAll(channelContext.getId(), channelContext);
	}

	// S


	/**
	 * 异步发送/同步发送 (使用同步发送时，在确保开启ACKPlugin后，只需要将Packet中Req字段赋值即可)
	 *
	 * @param channelContext 接收方通道
	 * @param packet         数据包
	 */
	public static boolean send(ChannelContext channelContext, Packet packet) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return send0(channelContext, packet, false);
	}

	private static boolean send0(ChannelContext channelContext, Packet packet, boolean isBlock) {
		return channelContext.sendPacket(packet, isBlock);
	}

	public static boolean sendToAll(AioConfig aioConfig, Packet packet) {
		if (aioConfig.isUseConnections()) {
			aioConfig.getConnections();
			return true;
		}else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("未开启保持连接状态");
			}
			 return false;
		}
	}

	public static boolean sendToAll(AioConfig aioConfig, Packet packet, ChannelContextFilter channelContextFilter) {
		return true;
	}

	public static boolean sendToAll(AioConfig aioConfig, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		if (aioConfig.isUseConnections()) {
			if (aioConfig.getConnections().size() > 0) {
			}else {
				LOGGER.debug("没人在线");
			}
			return true;
		}else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("未开启保持连接状态");
			}
			return false;
		}
	}

	public static boolean sendToSet(AioConfig aioConfig, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock){
		if (setWithLock.getObj().size() == 0) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{}没人在线", aioConfig.getName());
			}
			return false;
		}

		setWithLock.getObj().forEach(channelContext -> {
			if (Objects.isNull(channelContextFilter)) {
				if (channelContext != null) {
					send0(channelContext, packet, isBlock);
				}
			} else {
				if (channelContext != null && !channelContextFilter.filter(channelContext)) {
					send0(channelContext, packet, isBlock);
				}
			}
		});
		return true;
	}


	public static void sendToId(AioConfig config, String id, Packet packet) {
		ChannelContext Id = getChannelContextById(id, config);
		send(Id, packet);
	}

	public static void sendToId(AioConfig config, String id, Packet packet, boolean isBlock) {

	}


	/**
	 * 群发
	 *
	 * @param groupId        群组ID
	 * @param packet         消息包
	 * @param channelContext 发送者上下文
	 */
	public static void sendGroup(String groupId, Packet packet, ChannelContext channelContext, ChannelContextFilter channelContextFilter, boolean isBlock) {
		// 群发
		SetWithLock<?> set = channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.GROUP_ID).get(groupId, SetWithLock.class);

		set.getObj().forEach((Consumer<Object>) o -> {
			if (Objects.isNull(channelContextFilter)) {
				if (o instanceof ChannelContext) {
					send0((ChannelContext) o, packet, isBlock);
				}
			} else {
				if (o instanceof ChannelContext && !channelContextFilter.filter((ChannelContext) o)) {
					send0((ChannelContext) o, packet, isBlock);
				}
			}
		});
	}


//    public static boolean removeUserFromGroup(ChannelContext channelContext, String groupId) {
//        return channelContext.getAioConfig().getGroups().remove(groupId, channelContext);
//    }

	// U

	public static void unbindBsId(ChannelContext channelContext) {

	}

	private Aio() {

	}


}
