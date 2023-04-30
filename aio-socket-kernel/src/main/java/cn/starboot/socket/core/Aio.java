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
import cn.starboot.socket.maintain.MaintainEnum;
import cn.starboot.socket.utils.lock.ReadLockHandler;
import cn.starboot.socket.utils.lock.SetWithLock;
import cn.starboot.socket.utils.page.Page;
import cn.starboot.socket.utils.page.PageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

	public static Boolean bindBsId(String bsId,
								   ChannelContext channelContext) {
		if (Objects.isNull(bsId)
				|| Objects.isNull(channelContext)
				|| bsId.length() == 0)
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.join(bsId, channelContext);
	}

	public static Boolean bindCliNode(String cliNode,
									  ChannelContext channelContext) {
		if (Objects.isNull(cliNode)
				|| Objects.isNull(channelContext)
				|| cliNode.length() == 0)
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.join(cliNode, channelContext);
	}

	public static Boolean bindCluId(String cluId,
									ChannelContext channelContext) {
		if (Objects.isNull(cluId)
				|| Objects.isNull(channelContext)
				|| cluId.length() == 0)
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.join(cluId, channelContext);
	}

	public static Boolean bindGroup(String groupId,
									ChannelContext channelContext) {
		if (Objects.isNull(groupId)
				|| Objects.isNull(channelContext)
				|| groupId.length() == 0)
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.join(groupId, channelContext);
	}

	public static Boolean bindGroup(AioConfig aioConfig,
									String userId,
									String groupId) {
		return bindGroup(groupId, getChannelContextById(aioConfig, userId));
	}

	public static Boolean bindId(String id,
								 ChannelContext channelContext) {
		if (Objects.isNull(id)
				|| Objects.isNull(channelContext)
				|| id.length() == 0)
			return false;
		channelContext.setId(id);
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.join(id, channelContext);
	}

	public static Boolean bindIp(String ip,
								 ChannelContext channelContext) {
		if (Objects.isNull(ip)
				|| Objects.isNull(channelContext)
				|| ip.length() == 0)
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.join(ip, channelContext);
	}

	public static Boolean bindToken(String token,
									ChannelContext channelContext) {
		if (Objects.isNull(token)
				|| Objects.isNull(channelContext)
				|| token.length() == 0)
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.join(token, channelContext);
	}

	public static Boolean bindUser(String user,
								   ChannelContext channelContext) {
		if (Objects.isNull(user)
				|| Objects.isNull(channelContext)
				|| user.length() == 0)
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.join(user, channelContext);
	}

	// ********************************************* 阻塞发送篇
	public static Boolean bSend(ChannelContext channelContext,
								Packet packet) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return send0(channelContext, packet, true);
	}

	public static Boolean bSendToAll(AioConfig aioConfig,
									 Packet packet) {
		return bSendToAll(aioConfig, packet, null);
	}

	public static Boolean bSendToAll(AioConfig aioConfig,
									 Packet packet,
									 ChannelContextFilter channelContextFilter) {
		return sendToAll(aioConfig, packet, channelContextFilter, true);
	}

	public static Boolean bSendToBsId(AioConfig aioConfig,
									  String bsId,
									  Packet packet) {
		return sendToBsId(aioConfig, bsId, packet, true);
	}

	public static Boolean bSendToClientNode(AioConfig aioConfig,
											String ip,
											int port,
											Packet packet) {
		return sendToClientNode(aioConfig, ip, port, packet, true);
	}

	public static Boolean bSendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet) {
		return bSendToCluId(aioConfig, cluId, packet, null);
	}

	public static Boolean bSendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToCluId(aioConfig, cluId, packet, channelContextFilter, true);
	}

	public static Boolean bSendToGroup(String groupId,
									   Packet packet,
									   AioConfig aioConfig) {
		return bSendToGroup(aioConfig, groupId, packet, null);
	}

	public static Boolean bSendToGroup(AioConfig aioConfig,
									   String groupId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToGroup(aioConfig, groupId, packet, channelContextFilter, true);
	}

	public static Boolean bSendToId(AioConfig aioConfig,
									String bsId,
									Packet packet) {
		return sendToId(aioConfig, bsId, packet, true);
	}

	public static Boolean bSendToIp(AioConfig aioConfig,
									String cluId,
									Packet packet) {
		return bSendToIp(aioConfig, cluId, packet, null);
	}

	public static Boolean bSendToIp(AioConfig aioConfig,
									String cluId,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToIp(aioConfig, cluId, packet, channelContextFilter, true);
	}

	public static Boolean bSendToToken(AioConfig aioConfig,
									   String cluId,
									   Packet packet) {
		return bSendToToken(aioConfig, cluId, packet, null);
	}

	public static Boolean bSendToToken(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToToken(aioConfig, cluId, packet, channelContextFilter, true);
	}

	public static Boolean bSendToUser(AioConfig aioConfig,
									  String cluId,
									  Packet packet) {
		return bSendToUser(aioConfig, cluId, packet, null);
	}

	public static Boolean bSendToUser(AioConfig aioConfig,
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
		Boolean aBoolean = unbindFromAll(channelContext);
		// 停止各种处理器的运行
		channelContext.getDecodeTaskRunnable().setCanceled(true);
		channelContext.getHandlerTaskRunnable().setCanceled(true);
		channelContext.getSendTaskRunnable().setCanceled(true);
		// 设置关闭码
		if (Objects.isNull(closeCode)) {
			if (Objects.equals(channelContext.getCloseCode(), CloseCode.INIT_STATUS)) {
				channelContext.setCloseCode(CloseCode.NO_CODE);
			}
		} else {
			channelContext.setCloseCode(closeCode);
		}
		// 当前通道的所有状态已处理完成，执行断开操作
		channelContext.close();
	}

	public static void closeBsId(AioConfig aioConfig,
								 String bsId) {
		closeBsId(aioConfig, bsId, null);
	}

	public static void closeBsId(AioConfig aioConfig,
								 String bsId,
								 CloseCode closeCode) {
		close(getChannelContextByBsId(aioConfig, bsId), closeCode);
	}

	public static void closeClientNode(AioConfig aioConfig,
									   String clientNode) {
		closeClientNode(aioConfig, clientNode, null);
	}

	public static void closeClientNode(AioConfig aioConfig,
									   String clientNode,
									   CloseCode closeCode) {
		close(getChannelContextByClientNode(aioConfig, clientNode), closeCode);
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
		closeId(aioConfig, id, null);
	}

	public static void closeId(AioConfig aioConfig,
							   String id,
							   CloseCode closeCode) {
		close(getChannelContextById(aioConfig, id), closeCode);
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
								SetWithLock<ChannelContext> setWithLock,
								CloseCode closeCode) {
		if (Objects.isNull(setWithLock) || setWithLock.getObj().size() == 0) {
			return;
		}
		setWithLock.handle((ReadLockHandler<Set<ChannelContext>>)
				channelContextSet -> channelContextSet.forEach((Consumer<ChannelContext>)
						channelContext -> {
							if (Objects.nonNull(channelContext)) {
								close(channelContext, closeCode);
							}
						}));
	}

	// Get篇

	public static SetWithLock<ChannelContext> getAll(AioConfig aioConfig) {
		return aioConfig.getConnections();
	}

	public static SetWithLock<ChannelContext> getAllChannelContexts(AioConfig aioConfig) {
		return getAll(aioConfig);
	}

	public static ChannelContext getByBsId(AioConfig aioConfig,
										   String bsId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.getChannelContext(bsId);
	}

	public static ChannelContext getChannelContextByBsId(AioConfig aioConfig,
														 String bsId) {
		return getByBsId(aioConfig, bsId);
	}

	public static ChannelContext getByClientNode(AioConfig aioConfig,
												 String clientNode) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.getChannelContext(clientNode);
	}

	public static ChannelContext getChannelContextByClientNode(AioConfig aioConfig,
															   String clientNode) {
		return getByClientNode(aioConfig, clientNode);
	}

	public static SetWithLock<ChannelContext> getByCluId(AioConfig aioConfig,
														 String cluId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
	}

	public static SetWithLock<ChannelContext> getChannelContextByCluId(AioConfig aioConfig,
																	   String cluId) {
		return getByCluId(aioConfig, cluId);
	}

	public static SetWithLock<ChannelContext> getByGroupId(AioConfig aioConfig,
														   String groupId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
	}

	public static SetWithLock<ChannelContext> getChannelContextByGroupId(AioConfig aioConfig,
																		 String groupId) {
		return getByGroupId(aioConfig, groupId);
	}

	public static ChannelContext getById(AioConfig aioConfig,
										 String id) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.getChannelContext(id);
	}

	public static ChannelContext getChannelContextById(AioConfig aioConfig,
													   String id) {
		return getById(aioConfig, id);
	}

	public static SetWithLock<ChannelContext> getByIp(AioConfig aioConfig,
													  String ip) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
	}

	public static SetWithLock<ChannelContext> getChannelContextByIp(AioConfig aioConfig,
																	String ip) {
		return getByIp(aioConfig, ip);
	}

	public static SetWithLock<ChannelContext> getByToken(AioConfig aioConfig,
														 String token) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
	}

	public static SetWithLock<ChannelContext> getChannelContextByToken(AioConfig aioConfig,
																	   String token) {
		return getByToken(aioConfig, token);
	}

	public static SetWithLock<ChannelContext> getByUser(AioConfig aioConfig,
														String user) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
	}

	public static SetWithLock<ChannelContext> getChannelContextByUser(AioConfig aioConfig,
																	  String user) {
		return getByUser(aioConfig, user);
	}

	// 按照分页获取所有在线用户
	public static Page<ChannelContext> getPageOfAll(AioConfig aioConfig,
													Integer pageIndex,
													Integer pageSize) {
		SetWithLock<ChannelContext> connections = aioConfig.getConnections();
		return getPageOfSet(connections, pageIndex, pageSize);
	}

	public static Page<ChannelContext> getPageOfClu(AioConfig aioConfig,
													String cluId,
													Integer pageIndex,
													Integer pageSize) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		return getPageOfSet(setWithLock, pageIndex, pageSize);
	}

	public static Integer cluCount(AioConfig aioConfig,
								   String cluId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId)
				.size();
	}

	public static Boolean isInClu(AioConfig aioConfig,
								  String cluId,
								  ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		return isInSet(setWithLock, channelContext);
	}

	// 按照分页获取群组
	public static Page<ChannelContext> getPageOfGroup(AioConfig aioConfig,
													  String groupId,
													  Integer pageIndex,
													  Integer pageSize) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
		return getPageOfSet(setWithLock, pageIndex, pageSize);
	}

	// 群组有多少个连接
	public static Integer groupCount(AioConfig aioConfig,
									 String groupId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId)
				.size();
	}

	// 某通道是否在某群组中
	public static Boolean isInGroup(AioConfig aioConfig,
									String groupId,
									ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
		return isInSet(setWithLock, channelContext);
	}

	public static Page<ChannelContext> getPageOfIp(AioConfig aioConfig,
												   String ip,
												   Integer pageIndex,
												   Integer pageSize) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		return getPageOfSet(setWithLock, pageIndex, pageSize);
	}

	public static Integer ipCount(AioConfig aioConfig,
								  String ip) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip)
				.size();
	}

	public static Boolean isInIp(AioConfig aioConfig,
								 String ip,
								 ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		return isInSet(setWithLock, channelContext);
	}

	public static Page<ChannelContext> getPageOfToken(AioConfig aioConfig,
													  String token,
													  Integer pageIndex,
													  Integer pageSize) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		return getPageOfSet(setWithLock, pageIndex, pageSize);
	}

	public static Integer tokenCount(AioConfig aioConfig,
									 String token) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token)
				.size();
	}

	public static Boolean isInToken(AioConfig aioConfig,
									String token,
									ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		return isInSet(setWithLock, channelContext);
	}

	public static Page<ChannelContext> getPageOfUser(AioConfig aioConfig,
													 String user,
													 Integer pageIndex,
													 Integer pageSize) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		return getPageOfSet(setWithLock, pageIndex, pageSize);
	}

	public static Integer userCount(AioConfig aioConfig,
									String user) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user)
				.size();
	}

	public static Boolean isInUser(AioConfig aioConfig,
								   String user,
								   ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		return isInSet(setWithLock, channelContext);
	}

	private static Page<ChannelContext> getPageOfSet(SetWithLock<ChannelContext> setWithLock,
													 Integer pageIndex,
													 Integer pageSize) {
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize);
	}

	private static Boolean isInSet(SetWithLock<ChannelContext> setWithLock,
								   ChannelContext channelContext) {
		AtomicBoolean contains = new AtomicBoolean(false);
		setWithLock.handle((ReadLockHandler<Set<ChannelContext>>)
				channelContextSet -> contains.set(channelContextSet.contains(channelContext)));
		return contains.get();
	}

	// Remove 篇

	public static void remove(ChannelContext channelContext) {
		remove(channelContext, null);
	}

	public static void remove(ChannelContext channelContext,
							  CloseCode closeCode) {
		close(channelContext, closeCode);
	}

	public static void removeBsId(AioConfig aioConfig,
								  String bsId) {
		removeBsId(aioConfig, bsId, null);
	}

	public static void removeBsId(AioConfig aioConfig,
								  String bsId,
								  CloseCode closeCode) {
		remove(getChannelContextByBsId(aioConfig, bsId), closeCode);
	}

	public static void removeClientNode(AioConfig aioConfig,
										String clientNode) {
		removeClientNode(aioConfig, clientNode, null);
	}

	public static void removeClientNode(AioConfig aioConfig,
										String clientNode,
										CloseCode closeCode) {
		remove(getChannelContextByClientNode(aioConfig, clientNode), closeCode);
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
		removeId(aioConfig, id, null);
	}

	public static void removeId(AioConfig aioConfig,
								String id,
								CloseCode closeCode) {
		remove(getChannelContextById(aioConfig, id), closeCode);
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
								 SetWithLock<ChannelContext> setWithLock,
								 CloseCode closeCode) {
		closeSet(aioConfig, setWithLock, closeCode);
	}

	//                                Send 篇

	/**
	 * 异步发送/同步发送 (使用同步发送时，在确保开启ACKPlugin后，只需要将Packet中Req字段赋值即可)
	 *
	 * @param channelContext 接收方通道
	 * @param packet         数据包
	 */
	public static Boolean send(ChannelContext channelContext,
							   Packet packet) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return send0(channelContext, packet, false);
	}

	private static Boolean send0(ChannelContext channelContext,
								 Packet packet,
								 boolean isBlock) {
		return channelContext.sendPacket(packet, isBlock);
	}

	public static Boolean sendToAll(AioConfig aioConfig,
									Packet packet) {
		return sendToAll(aioConfig, packet, null);
	}

	public static Boolean sendToAll(AioConfig aioConfig,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToAll(aioConfig, packet, channelContextFilter, false);
	}

	public static Boolean sendToAll(AioConfig aioConfig,
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

	public static Boolean sendToBsId(AioConfig aioConfig,
									 String bsId,
									 Packet packet) {
		return sendToBsId(aioConfig, bsId, packet, false);
	}

	private static Boolean sendToBsId(AioConfig aioConfig,
									  String bsId,
									  Packet packet,
									  boolean isBlock) {
		ChannelContext channelContext = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.getChannelContext(bsId);
		return send0(channelContext, packet, isBlock);
	}

	public static Boolean sendToClientNode(AioConfig aioConfig,
										   String ip,
										   int port,
										   Packet packet) {
		return sendToClientNode(aioConfig, ip, port, packet, false);
	}

	private static Boolean sendToClientNode(AioConfig aioConfig,
											String ip,
											int port,
											Packet packet,
											boolean isBlock) {
		ChannelContext channelContext = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.getChannelContext(ip + port);
		return send0(channelContext, packet, isBlock);
	}

	public static Boolean sendToCluId(AioConfig aioConfig,
									  String cluId,
									  Packet packet) {
		return sendToCluId(aioConfig, cluId, packet, null);
	}

	public static Boolean sendToCluId(AioConfig aioConfig,
									  String cluId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToCluId(aioConfig, cluId, packet, channelContextFilter, false);
	}

	private static Boolean sendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter,
									   boolean isBlock) {
		SetWithLock<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		if (Objects.isNull(set)) {
			LOGGER.info("该cluId没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}


	public static Boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet) {
		return sendToGroup(aioConfig, groupId, packet, null);
	}

	public static Boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToGroup(aioConfig, groupId, packet, channelContextFilter, false);
	}

	/**
	 * 群发
	 *
	 * @param groupId   群组ID
	 * @param packet    消息包
	 * @param aioConfig 发送者上下文
	 */
	public static Boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter,
									  boolean isBlock) {
		// 群组成员集合
		SetWithLock<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
		if (Objects.isNull(set)) {
			LOGGER.info("该groupId没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	public static Boolean sendToId(AioConfig config,
								   String id,
								   Packet packet) {
		return sendToId(config, id, packet, false);
	}

	private static Boolean sendToId(AioConfig config,
									String id,
									Packet packet,
									boolean isBlock) {
		return send0(getChannelContextById(config, id), packet, isBlock);
	}

	public static Boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet) {
		return sendToIp(aioConfig, ip, packet, null);
	}

	public static Boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet,
								   ChannelContextFilter channelContextFilter) {
		return sendToIp(aioConfig, ip, packet, channelContextFilter, false);
	}

	public static Boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet,
								   ChannelContextFilter channelContextFilter,
								   boolean isBlock) {
		SetWithLock<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		if (Objects.isNull(set)) {
			LOGGER.info("该ip没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	public static Boolean sendToSet(AioConfig aioConfig,
									SetWithLock<ChannelContext> setWithLock,
									Packet packet) {
		return sendToSet(aioConfig, setWithLock, packet, null);
	}

	public static Boolean sendToSet(AioConfig aioConfig,
									SetWithLock<ChannelContext> setWithLock,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToSet(aioConfig, setWithLock, packet, channelContextFilter, false);
	}

	private static Boolean sendToSet(AioConfig aioConfig,
									 SetWithLock<ChannelContext> setWithLock,
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
			setWithLock
					.handle((ReadLockHandler<Set<ChannelContext>>)
							channelContextSet -> channelContextSet.forEach(
									channelContext -> {
										if (Objects.nonNull(channelContext)) {
											sendNum.increment();
											if (send0(channelContext, packet, isBlock)) {
												sendSuc.increment();
											}
										}
									}));
		} else {
			setWithLock
					.handle((ReadLockHandler<Set<ChannelContext>>)
							channelContextSet -> channelContextSet.forEach(
									channelContext -> {
										if (Objects.nonNull(channelContext)
												&& channelContextFilter.filter(channelContext)) {
											sendNum.increment();
											if (send0(channelContext, packet, isBlock)) {
												sendSuc.increment();
											}
										}
									}));
		}
		return sendNum.longValue() == sendSuc.longValue();
	}

	public static Boolean sendToToken(AioConfig aioConfig,
									  String token,
									  Packet packet) {
		return sendToToken(aioConfig, token, packet, null);
	}

	public static Boolean sendToToken(AioConfig aioConfig,
									  String token,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToToken(aioConfig, token, packet, channelContextFilter, false);
	}

	private static Boolean sendToToken(AioConfig aioConfig,
									   String token,
									   Packet packet,
									   ChannelContextFilter channelContextFilter,
									   boolean isBlock) {
		SetWithLock<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		if (Objects.isNull(set)) {
			LOGGER.info("该token没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	public static Boolean sendToUser(AioConfig aioConfig,
									 String user,
									 Packet packet) {
		return sendToUser(aioConfig, user, packet, null);
	}

	public static Boolean sendToUser(AioConfig aioConfig,
									 String user,
									 Packet packet,
									 ChannelContextFilter channelContextFilter) {
		return sendToUser(aioConfig, user, packet, channelContextFilter, false);
	}

	private static Boolean sendToUser(AioConfig aioConfig,
									  String user,
									  Packet packet,
									  ChannelContextFilter channelContextFilter,
									  boolean isBlock) {
		SetWithLock<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		if (Objects.isNull(set)) {
			LOGGER.info("该user没有绑定任何通道");
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	// UnBing篇

	public static Boolean unbindFromAll(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return unbindBsId(channelContext)
				&& unbindClientNode(channelContext)
				&& unbindFromAllClu(channelContext)
				&& unbindFromAllGroup(channelContext)
				&& unbindId(channelContext)
				&& unbindFromAllIp(channelContext)
				&& unbindFromAllToken(channelContext)
				&& unbindFromAllUser(channelContext);
	}

	public static Boolean unbindBsId(ChannelContext channelContext) {
		return unbindBsId(channelContext.getAioConfig(), null, channelContext);
	}

	public static Boolean unbindBsId(AioConfig aioConfig,
									 String bsId) {
		return unbindBsId(aioConfig, bsId, null);
	}

	public static Boolean unbindBsId(AioConfig aioConfig,
									 String bsId,
									 ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.remove(bsId, channelContext);
	}

	public static Boolean unbindClientNode(ChannelContext channelContext) {
		return unbindClientNode(channelContext.getAioConfig(), null, channelContext);
	}

	public static Boolean unbindClientNode(AioConfig aioConfig,
										   String cliNode) {
		return unbindClientNode(aioConfig, cliNode, null);
	}

	public static Boolean unbindClientNode(AioConfig aioConfig,
										   String cliNode,
										   ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.remove(cliNode, channelContext);
	}

	public static Boolean unbindClu(String cluId,
									ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext.
				getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.remove(cluId, channelContext);
	}

	public static Boolean unbindFromAllClu(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.removeAll(channelContext);
	}

	public static Boolean unbindGroup(String groupId,
									  ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.remove(groupId, channelContext);
	}

	public static Boolean unbindFromAllGroup(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.removeAll(channelContext);
	}

	public static Boolean unbindId(ChannelContext channelContext) {
		return unbindId(channelContext.getAioConfig(), null, channelContext);
	}

	public static Boolean unbindId(AioConfig aioConfig,
								   String id) {
		return unbindId(aioConfig, id, null);
	}

	public static Boolean unbindId(AioConfig aioConfig,
								   String id,
								   ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.remove(id, channelContext);
	}

	public static Boolean unbindIp(String ip,
								   ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.remove(ip, channelContext);
	}

	public static Boolean unbindFromAllIp(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.removeAll(channelContext);
	}

	public static Boolean unbindToken(String token,
									  ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.remove(token, channelContext);
	}

	public static Boolean unbindFromAllToken(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.removeAll(channelContext);
	}

	public static Boolean unbindUser(String user,
									 ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.remove(user, channelContext);
	}

	public static Boolean unbindFromAllUser(ChannelContext channelContext) {
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
	private Aio() {
	}

}
