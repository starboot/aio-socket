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
import java.util.concurrent.atomic.LongAdder;
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

	public static boolean bindBsId(String bsId,
								   ChannelContext channelContext) {
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.join(bsId, channelContext);
	}

	public static boolean bindCliNode(String cliNode,
									  ChannelContext channelContext) {
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.join(cliNode, channelContext);
	}

	public static boolean bindCluId(String cluId,
									ChannelContext channelContext) {
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.join(cluId, channelContext);
	}

	public static boolean bindGroup(String groupId,
									ChannelContext channelContext) {
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.join(groupId, channelContext);
	}

	/**
	 *
	 * @param aioConfig
	 * @param userId
	 * @param groupId
	 * @param maintainEnum
	 * @return
	 */
	public static boolean bindGroup(AioConfig aioConfig,
									String userId,
									String groupId,
									MaintainEnum maintainEnum) {
		return bindGroup(groupId, getChannelContextById(aioConfig, userId));
	}

	public static boolean bindId(String id,
								 ChannelContext channelContext) {
		channelContext.setId(id);
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.join(id, channelContext);
	}

	public static boolean bindIp(String ip,
								 ChannelContext channelContext) {
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.join(ip, channelContext);
	}

	public static boolean bindToken(String token,
									ChannelContext channelContext) {
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.join(token, channelContext);
	}

	public static boolean bindUser(String user,
								   ChannelContext channelContext) {
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.join(user, channelContext);
	}

	// ********************************************* 阻塞发送篇
	public static boolean bSend(ChannelContext channelContext,
								Packet packet) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return send0(channelContext, packet, true);
	}

	public static boolean bSendToAll(AioConfig aioConfig,
									 Packet packet) {
		return bSendToAll(aioConfig, packet, null);
	}

	public static boolean bSendToAll(AioConfig aioConfig,
									 Packet packet,
									 ChannelContextFilter channelContextFilter) {
		return sendToAll(aioConfig, packet, channelContextFilter, true);
	}

	public static boolean bSendToBsId(AioConfig aioConfig,
									  String bsId,
									  Packet packet) {
		return sendToBsId(aioConfig, bsId, packet, true);
	}

	public static boolean bSendToClientNode(AioConfig aioConfig,
											String ip,
											int port,
											Packet packet) {
		return sendToClientNode(aioConfig, ip, port, packet, true);
	}

	public static boolean bSendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet) {
		return bSendToCluId(aioConfig, cluId, packet, null);
	}

	public static boolean bSendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToCluId(aioConfig, cluId, packet, channelContextFilter, true);
	}

	public static boolean bSendToGroup(String groupId,
									   Packet packet,
									   AioConfig aioConfig) {
		return bSendToGroup(aioConfig, groupId, packet, null);
	}

	public static boolean bSendToGroup(AioConfig aioConfig,
									   String groupId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToGroup(aioConfig, groupId, packet, channelContextFilter, true);
	}

	public static boolean bSendToId(AioConfig aioConfig,
									String bsId,
									Packet packet) {
		return sendToId(aioConfig, bsId, packet, true);
	}

	public static boolean bSendToIp(AioConfig aioConfig,
									String cluId,
									Packet packet) {
		return bSendToIp(aioConfig, cluId, packet, null);
	}

	public static boolean bSendToIp(AioConfig aioConfig,
									String cluId,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToIp(aioConfig, cluId, packet, channelContextFilter, true);
	}

	public static boolean bSendToToken(AioConfig aioConfig,
									   String cluId,
									   Packet packet) {
		return bSendToToken(aioConfig, cluId, packet, null);
	}

	public static boolean bSendToToken(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToToken(aioConfig, cluId, packet, channelContextFilter, true);
	}

	public static boolean bSendToUser(AioConfig aioConfig,
									  String cluId,
									  Packet packet) {
		return bSendToUser(aioConfig, cluId, packet, null);
	}

	public static boolean bSendToUser(AioConfig aioConfig,
									  String cluId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToUser(aioConfig, cluId, packet, channelContextFilter, true);
	}

	//                              close篇

	public static void close(ChannelContext channelContext) {
		close(channelContext, null);
	}

	public static void close(ChannelContext channelContext,
							 CloseCode closeCode) {
		if (Objects.isNull(channelContext)) {
			return;
		}
		// 从各个关系中移除
		boolean b = removeUserFromAllGroup(channelContext);
		channelContext.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.remove(channelContext.getId(), channelContext);
		// 停止各种处理器的运行
		channelContext.getDecodeTaskRunnable().setCanceled(true);
		channelContext.getHandlerTaskRunnable().setCanceled(true);
		channelContext.getSendTaskRunnable().setCanceled(true);

		if (Objects.isNull(closeCode)) {
			if (Objects.equals(channelContext.getCloseCode(), CloseCode.INIT_STATUS)) {
				channelContext.setCloseCode(CloseCode.NO_CODE);
			}
		}else {
			channelContext.setCloseCode(closeCode);
		}
		// 当前通道的所有状态已处理完成，执行断开操作
		channelContext.close();
	}

	public static void closeBsId(AioConfig aioConfig,
								 String bsId) {
		close(getChannelContextByBsId(aioConfig, bsId));
	}

	public static void closeClientNode(AioConfig aioConfig,
									   String clientNode) {
		close(getChannelContextByClientNode(aioConfig, clientNode));
	}

	public static void closeClu(AioConfig aioConfig,
								String cluId) {
		closeClu(aioConfig, cluId, null);
	}

	public static void closeClu(AioConfig aioConfig,
								String cluId,
								CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByCluId(aioConfig, cluId), closeCode);
	}

	public static void closeGroup(AioConfig aioConfig,
								  String groupId) {
		closeGroup(aioConfig, groupId, null);
	}

	public static void closeGroup(AioConfig aioConfig,
								  String groupId,
								  CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByGroupId(aioConfig, groupId), closeCode);
	}

	public static void closeId(AioConfig aioConfig,
							   String id) {
		close(getChannelContextById(aioConfig, id));
	}

	public static void closeIp(AioConfig aioConfig,
							   String ip) {
		closeIp(aioConfig, ip, null);
	}

	public static void closeIp(AioConfig aioConfig,
							   String ip,
							   CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByIp(aioConfig, ip), closeCode);
	}

	public static void closeToken(AioConfig aioConfig,
								  String token) {
		closeToken(aioConfig, token, null);
	}

	public static void closeToken(AioConfig aioConfig,
								  String token,
								  CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByToken(aioConfig, token), closeCode);
	}

	public static void closeUser(AioConfig aioConfig,
								 String user) {
		closeUser(aioConfig, user, null);
	}

	public static void closeUser(AioConfig aioConfig,
								 String user,
								 CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByUser(aioConfig, user), closeCode);
	}

	public static void closeSet(AioConfig aioConfig,
								SetWithLock<?> setWithLock,
								CloseCode closeCode) {
		if (Objects.isNull(setWithLock) || setWithLock.getObj().size() == 0) {
			return;
		}
		setWithLock.getObj().forEach((Consumer<Object>) object -> {
			if (Objects.nonNull(object) && object instanceof ChannelContext) {
				close((ChannelContext) object,closeCode);
			}
		});
	}

	// Get篇

	public static SetWithLock<ChannelContext> getAll(AioConfig aioConfig) {
		return aioConfig.getConnections();
	}

	public static SetWithLock<ChannelContext> getAllChannelContexts(AioConfig aioConfig) {
		return getAll(aioConfig);
	}

	public static ChannelContext getByBsId(AioConfig aioConfig, String bsId) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.Bs_ID).get(bsId, ChannelContext.class);
	}

	public static ChannelContext getChannelContextByBsId(AioConfig aioConfig, String bsId) {
		return getByBsId(aioConfig, bsId);
	}

	public static ChannelContext getByClientNode(AioConfig aioConfig, String clientNode) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.CLIENT_NODE_ID).get(clientNode, ChannelContext.class);
	}

	public static ChannelContext getChannelContextByClientNode(AioConfig aioConfig, String clientNode) {
		return getByClientNode(aioConfig, clientNode);
	}

	public static SetWithLock<?> getByCluId(AioConfig aioConfig, String cluId) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.CLU_ID).get(cluId, SetWithLock.class);
	}

	public static SetWithLock<?> getChannelContextByCluId(AioConfig aioConfig, String cluId) {
		return getByCluId(aioConfig, cluId);
	}

	public static SetWithLock<?> getByGroupId(AioConfig aioConfig, String groupId) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.GROUP_ID).get(groupId, SetWithLock.class);
	}

	public static SetWithLock<?> getChannelContextByGroupId(AioConfig aioConfig, String groupId) {
		return getByGroupId(aioConfig, groupId);
	}

	public static ChannelContext getById(AioConfig aioConfig, String id) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.ID).get(id, ChannelContext.class);
	}

	public static ChannelContext getChannelContextById(AioConfig aioConfig, String id) {
		return getById(aioConfig, id);
	}

	public static SetWithLock<?> getByIp(AioConfig aioConfig, String ip) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.IP).get(ip, SetWithLock.class);
	}

	public static SetWithLock<?> getChannelContextByIp(AioConfig aioConfig, String ip) {
		return getByIp(aioConfig, ip);
	}

	public static SetWithLock<?> getByToken(AioConfig aioConfig, String token) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.TOKEN).get(token, SetWithLock.class);
	}

	public static SetWithLock<?> getChannelContextByToken(AioConfig aioConfig, String token) {
		return getByToken(aioConfig, token);
	}

	public static SetWithLock<?> getByUser(AioConfig aioConfig, String user) {
		return aioConfig.getMaintainManager().getCommand(MaintainEnum.USER).get(user, SetWithLock.class);
	}

	public static SetWithLock<?> getChannelContextByUser(AioConfig aioConfig, String user) {
		return getByUser(aioConfig, user);
	}

	// 按照分页获取所有在线用户
	public static void getPageOfAll(AioConfig aioConfig, Integer pageIndex, Integer pageSize) {

	}

	// 按照分页获取群组
	public static void getPageOfGroup(AioConfig aioConfig, String group, Integer pageIndex, Integer pageSize) {

	}

	// 群组有多少个连接
	public static void groupCount(AioConfig aioConfig, String groupId) {

	}

	// 某通道是否在某群组中
	public static void isInGroup(String groupId, ChannelContext channelContext) {

	}

	// 所有一对多的都需要有此方法

	// Remove
	public static boolean removeUserFromAllGroup(ChannelContext channelContext) {
		return channelContext.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.removeAll(channelContext);
	}

	public static void remove(ChannelContext channelContext) {
		remove(channelContext, null);
	}

	public static void remove(ChannelContext channelContext,
							  CloseCode closeCode) {
		close(channelContext, closeCode);
	}

	public static void removeBsId(AioConfig aioConfig,
								  String bsId) {
		remove(getChannelContextByBsId(aioConfig, bsId));
	}

	public static void removeClientNode(AioConfig aioConfig,
										String clientNode) {
		remove(getChannelContextByClientNode(aioConfig, clientNode));
	}

	public static void removeClu(AioConfig aioConfig,
								 String cluId) {
		removeClu(aioConfig, cluId, null);
	}

	public static void removeClu(AioConfig aioConfig,
								 String cluId,
								 CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByCluId(aioConfig, cluId), closeCode);
	}

	public static void removeGroup(AioConfig aioConfig,
								   String groupId) {
		removeGroup(aioConfig, groupId, null);
	}

	public static void removeGroup(AioConfig aioConfig,
								   String groupId,
								   CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByGroupId(aioConfig, groupId), closeCode);
	}

	public static void removeId(AioConfig aioConfig,
								String id) {
		remove(getChannelContextById(aioConfig, id));
	}

	public static void removeIp(AioConfig aioConfig,
								String ip) {
		removeIp(aioConfig, ip, null);
	}

	public static void removeIp(AioConfig aioConfig,
								String ip,
								CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByIp(aioConfig, ip), closeCode);
	}

	public static void removeToken(AioConfig aioConfig,
								   String token) {
		removeToken(aioConfig, token, null);
	}

	public static void removeToken(AioConfig aioConfig,
								   String token,
								   CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByToken(aioConfig, token), closeCode);
	}

	public static void removeUser(AioConfig aioConfig,
								  String user) {
		removeUser(aioConfig, user, null);
	}

	public static void removeUser(AioConfig aioConfig,
								  String user,
								  CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByUser(aioConfig, user), closeCode);
	}

	public static void removeSet(AioConfig aioConfig,
								 SetWithLock<?> setWithLock,
								 CloseCode closeCode) {
		closeSet(aioConfig, setWithLock, closeCode);
	}

	// ***************************************************              Send 篇

	/**
	 * 异步发送/同步发送 (使用同步发送时，在确保开启ACKPlugin后，只需要将Packet中Req字段赋值即可)
	 *
	 * @param channelContext 接收方通道
	 * @param packet         数据包
	 */
	public static boolean send(ChannelContext channelContext,
							   Packet packet) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return send0(channelContext, packet, false);
	}

	private static boolean send0(ChannelContext channelContext,
								 Packet packet, boolean isBlock) {
		return channelContext.sendPacket(packet, isBlock);
	}

	public static boolean sendToAll(AioConfig aioConfig,
									Packet packet) {
		return sendToAll(aioConfig, packet, null);
	}

	public static boolean sendToAll(AioConfig aioConfig,
									Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToAll(aioConfig, packet, channelContextFilter, false);
	}

	public static boolean sendToAll(AioConfig aioConfig,
									Packet packet,
									ChannelContextFilter channelContextFilter,
									boolean isBlock) {
		if (aioConfig.isUseConnections()) {
			if (aioConfig.getConnections().size() > 0) {
				sendToSet(aioConfig, aioConfig.getConnections(), packet, channelContextFilter, isBlock);
			} else {
				LOGGER.debug("没人在线");
			}
			return true;
		} else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("未开启保持连接状态");
			}
			return false;
		}
	}

	public static boolean sendToBsId(AioConfig aioConfig,
									 String bsId,
									 Packet packet) {
		return sendToBsId(aioConfig, bsId, packet, false);
	}

	private static boolean sendToBsId(AioConfig aioConfig,
									  String bsId,
									  Packet packet,
									  boolean isBlock) {
		ChannelContext channelContext = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.get(bsId, ChannelContext.class);
		return send0(channelContext, packet, isBlock);
	}

	public static boolean sendToClientNode(AioConfig aioConfig,
										   String ip,
										   int port,
										   Packet packet) {
		return sendToClientNode(aioConfig, ip, port, packet, false);
	}

	private static boolean sendToClientNode(AioConfig aioConfig,
											String ip,
											int port,
											Packet packet,
											boolean isBlock) {
		ChannelContext channelContext = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.get(ip + port, ChannelContext.class);
		return send0(channelContext, packet, isBlock);
	}

	public static boolean sendToCluId(AioConfig aioConfig,
									  String cluId,
									  Packet packet) {
		return sendToCluId(aioConfig, cluId, packet, null);
	}

	public static boolean sendToCluId(AioConfig aioConfig,
									  String cluId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToCluId(aioConfig, cluId, packet, channelContextFilter, false);
	}

	private static boolean sendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter,
									   boolean isBlock) {
		SetWithLock<?> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.get(cluId, SetWithLock.class);
		if (Objects.isNull(set)) {
			LOGGER.info("该cluId没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}


	public static boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet) {
		return sendToGroup(aioConfig, groupId, packet, null);
	}

	public static boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToGroup(aioConfig, groupId, packet, channelContextFilter, false);
	}

	/**
	 * 群发
	 *
	 * @param groupId        群组ID
	 * @param packet         消息包
	 * @param aioConfig 发送者上下文
	 */
	public static boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter,
									  boolean isBlock) {
		// 群组成员集合
		SetWithLock<?> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.get(groupId, SetWithLock.class);
		if (Objects.isNull(set)) {
			LOGGER.info("该groupId没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	public static boolean sendToId(AioConfig config,
								   String id,
								   Packet packet) {
		return sendToId(config, id, packet, false);
	}

	private static boolean sendToId(AioConfig config,
									String id,
									Packet packet,
									boolean isBlock) {
		return send0(getChannelContextById(config, id), packet, isBlock);
	}

	public static boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet) {
		return sendToIp(aioConfig, ip, packet, null);
	}

	public static boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet,
								   ChannelContextFilter channelContextFilter) {
		return sendToIp(aioConfig, ip, packet, channelContextFilter, false);
	}

	public static boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet,
								   ChannelContextFilter channelContextFilter,
								   boolean isBlock) {
		SetWithLock<?> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.get(ip, SetWithLock.class);
		if (Objects.isNull(set)) {
			LOGGER.info("该ip没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	public static boolean sendToSet(AioConfig aioConfig,
									SetWithLock<?> setWithLock,
									Packet packet) {
		return sendToSet(aioConfig, setWithLock, packet, null);
	}

	public static boolean sendToSet(AioConfig aioConfig,
									SetWithLock<?> setWithLock,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToSet(aioConfig, setWithLock, packet, channelContextFilter, false);
	}

	private static boolean sendToSet(AioConfig aioConfig,
									 SetWithLock<?> setWithLock,
									 Packet packet,
									 ChannelContextFilter channelContextFilter,
									 boolean isBlock) {
		if (Objects.isNull(setWithLock) || setWithLock.getObj().size() == 0) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{}, 没人在线", aioConfig.getName());
			}
			return false;
		}
		LongAdder sendNum = new LongAdder();
		LongAdder sendSuc = new LongAdder();
		if (Objects.isNull(channelContextFilter)) {
			setWithLock.getObj().forEach((Consumer<Object>) object -> {
				if (Objects.nonNull(object) && object instanceof ChannelContext) {
					sendNum.increment();
					if (send0((ChannelContext) object, packet, isBlock)) {
						sendSuc.increment();
					}
				}
			});
		}else {
			setWithLock.getObj().forEach((Consumer<Object>) object -> {
				if (Objects.nonNull(object) && object instanceof ChannelContext
						&& channelContextFilter.filter((ChannelContext) object)) {
					sendNum.increment();
					if (send0((ChannelContext) object, packet, isBlock)) {
						sendSuc.increment();
					}
				}
			});
		}
		return sendNum.longValue() == sendSuc.longValue();
	}

	public static boolean sendToToken(AioConfig aioConfig,
									  String token,
									  Packet packet) {
		return sendToToken(aioConfig, token, packet, null);
	}

	public static boolean sendToToken(AioConfig aioConfig, String token, Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToToken(aioConfig, token, packet, channelContextFilter, false);
	}

	private static boolean sendToToken(AioConfig aioConfig,
									   String token,
									   Packet packet,
									   ChannelContextFilter channelContextFilter,
									   boolean isBlock) {
		SetWithLock<?> set = aioConfig.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.get(token, SetWithLock.class);
		if (Objects.isNull(set)) {
			LOGGER.info("该token没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	public static boolean sendToUser(AioConfig aioConfig,
									 String user,
									 Packet packet) {
		return sendToUser(aioConfig, user, packet, null);
	}

	public static boolean sendToUser(AioConfig aioConfig,
									 String user,
									 Packet packet,
									 ChannelContextFilter channelContextFilter) {
		return sendToUser(aioConfig, user, packet, channelContextFilter, false);
	}

	private static boolean sendToUser(AioConfig aioConfig,
									  String user,
									  Packet packet,
									  ChannelContextFilter channelContextFilter,
									  boolean isBlock) {
		SetWithLock<?> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.get(user, SetWithLock.class);
		if (Objects.isNull(set)) {
			LOGGER.info("该user没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	// UnBing篇

	public static boolean unbindFromAll(AioConfig aioConfig,
										ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return unbindBsId(aioConfig, "", channelContext) &&
				unbindClientNode(aioConfig, "", channelContext) &&
				unbindFromAllClu(channelContext) &&
				unbindFromAllGroup(channelContext) &&
				unbindId(aioConfig, "", channelContext) &&
				unbindFromAllIp(channelContext) &&
				unbindFromAllToken(channelContext) &&
				unbindFromAllUser(channelContext);
	}

	public static boolean unbindBsId(AioConfig aioConfig,
									 String bsId) {
		return unbindBsId(aioConfig, bsId, null);
	}

	public static boolean unbindBsId(AioConfig aioConfig,
									 String bsId,
									 ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.remove(bsId, channelContext);
	}

	public static boolean unbindClientNode(AioConfig aioConfig,
										   String cliNode) {
		return unbindClientNode(aioConfig, cliNode, null);
	}

	public static boolean unbindClientNode(AioConfig aioConfig,
										   String cliNode,
										   ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.remove(cliNode, channelContext);
	}

	public static boolean unbindClu(String cluId,
									ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext.
				getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.remove(cluId, channelContext);
	}

	public static boolean unbindFromAllClu(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.removeAll(channelContext);
	}

	public static boolean unbindGroup(String groupId,
									  ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.remove(groupId, channelContext);
	}

	public static boolean unbindFromAllGroup(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.removeAll(channelContext);
	}

	public static boolean unbindId(AioConfig aioConfig,
								   String id) {
		return unbindId(aioConfig, id, null);
	}

	public static boolean unbindId(AioConfig aioConfig,
								   String id,
								   ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.remove(id, channelContext);
	}

	public static boolean unbindIp(String ip,
								   ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.remove(ip, channelContext);
	}

	public static boolean unbindFromAllIp(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.removeAll(channelContext);
	}

	public static boolean unbindToken(String token,
									  ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.remove(token, channelContext);
	}

	public static boolean unbindFromAllToken(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.removeAll(channelContext);
	}

	public static boolean unbindUser(String user,
									 ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.remove(user, channelContext);
	}

	public static boolean unbindFromAllUser(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.removeAll(channelContext);
	}

	/**
	 * 禁止实例化
	 */
	private Aio() { }

}
