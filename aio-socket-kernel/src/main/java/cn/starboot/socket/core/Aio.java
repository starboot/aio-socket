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
 * aio-socket基础常用API
 * 绑定、绑定
 * 单发（同步发送，异步发送）
 * 群发
 * 移除连接、关闭连接
 * GET：获取指定群组或Id的ChannelContext or ChannelContexts
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Aio {

	private static final Logger LOGGER = LoggerFactory.getLogger(Aio.class);

	// ---------------------------------绑定篇--------------------------------

	/**
	 * 绑定业务ID
	 *
	 * @param bsId 业务ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return {@code true} 绑定成功 或者
	 * 		   {@code false} 绑定失败
	 */
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

	/**
	 * 绑定客户端ip+端口
	 *
	 * @param cliNode 客户端
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
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

	/**
	 * 绑定集群ID
	 *
	 * @param cluId 集群ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
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

	/**
	 * 绑定群组：提供群组ID和用户上下文信息
	 *
	 * @param groupId 群组ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
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

	/**
	 * 绑定群组：不提供群组ID和用户上下文信息
	 *
	 * @param aioConfig 配置信息
	 * @param userId 用户ID
	 * @param groupId 群组ID
	 * @return 绑定状态
	 */
	public static Boolean bindGroup(AioConfig aioConfig,
									String userId,
									String groupId) {
		return bindGroup(groupId, getChannelContextById(aioConfig, userId));
	}

	/**
	 * 绑定ID
	 *
	 * @param id ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
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

	/**
	 * 绑定IP
	 *
	 * @param ip IP
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
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

	/**
	 * 绑定token
	 *
	 * @param token TOKEN
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
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

	/**
	 * 绑定用户
	 *
	 * @param user USER
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
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

	// -------------------------------同步发送篇-------------------------------

	/**
	 * 同步发送到指定用户
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSend(ChannelContext channelContext,
								Packet packet) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return send0(channelContext, packet, true);
	}

	/**
	 * 同步发送到平台所有用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToAll(AioConfig aioConfig,
									 Packet packet) {
		return bSendToAll(aioConfig, packet, null);
	}

	/**
	 * 同步发送到平台所有用户，并且带有过滤规则
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @param channelContextFilter 规则过滤器 {@link cn.starboot.socket.core.ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean bSendToAll(AioConfig aioConfig,
									 Packet packet,
									 ChannelContextFilter channelContextFilter) {
		return sendToAll(aioConfig, packet, channelContextFilter, true);
	}

	/**
	 * 同步发送到指定业务ID
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId 业务ID
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToBsId(AioConfig aioConfig,
									  String bsId,
									  Packet packet) {
		return sendToBsId(aioConfig, bsId, packet, true);
	}

	/**
	 * 同步发送到指定用户节点
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip IP
	 * @param port 端口
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToClientNode(AioConfig aioConfig,
											String ip,
											int port,
											Packet packet) {
		return sendToClientNode(aioConfig, ip, port, packet, true);
	}

	/**
	 * 同步发送到指定集群中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId 集群ID
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet) {
		return bSendToCluId(aioConfig, cluId, packet, null);
	}

	/**
	 * 同步发送到指定集群中，并且带有过滤规则
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId 集群ID
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @param channelContextFilter 规则过滤器 {@link cn.starboot.socket.core.ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean bSendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToCluId(aioConfig, cluId, packet, channelContextFilter, true);
	}

	/**
	 * 同步发送到指定群组中
	 *
	 * @param groupId 群组ID
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @return 发送状态
	 */
	public static Boolean bSendToGroup(String groupId,
									   Packet packet,
									   AioConfig aioConfig) {
		return bSendToGroup(aioConfig, groupId, packet, null);
	}

	/**
	 * 同步发送到指定群组中，并且带有过滤规则
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId 群组ID
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @param channelContextFilter 规则过滤器 {@link cn.starboot.socket.core.ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean bSendToGroup(AioConfig aioConfig,
									   String groupId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToGroup(aioConfig, groupId, packet, channelContextFilter, true);
	}

	/**
	 * 同步发送到指定ID
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id ID
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToId(AioConfig aioConfig,
									String id,
									Packet packet) {
		return sendToId(aioConfig, id, packet, true);
	}

	/**
	 * 同步发送到指定IP中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip IP
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToIp(AioConfig aioConfig,
									String ip,
									Packet packet) {
		return bSendToIp(aioConfig, ip, packet, null);
	}

	/**
	 * 同步发送到指定IP中，并且带有过滤规则
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip IP
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @param channelContextFilter 规则过滤器 {@link cn.starboot.socket.core.ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean bSendToIp(AioConfig aioConfig,
									String ip,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToIp(aioConfig, ip, packet, channelContextFilter, true);
	}

	/**
	 * 同步发送到指定TOKEN中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token TOKEN
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToToken(AioConfig aioConfig,
									   String token,
									   Packet packet) {
		return bSendToToken(aioConfig, token, packet, null);
	}

	/**
	 * 同步发送到指定TOKEN中，并且带有过滤规则
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token TOKEN
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @param channelContextFilter 规则过滤器 {@link cn.starboot.socket.core.ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean bSendToToken(AioConfig aioConfig,
									   String token,
									   Packet packet,
									   ChannelContextFilter channelContextFilter) {
		return sendToToken(aioConfig, token, packet, channelContextFilter, true);
	}

	/**
	 * 同步发送到指定用户中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user USER
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToUser(AioConfig aioConfig,
									  String user,
									  Packet packet) {
		return bSendToUser(aioConfig, user, packet, null);
	}

	/**
	 * 同步发送到指定用户中，并且带有过滤规则
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user USER
	 * @param packet 数据报文 {@link cn.starboot.socket.Packet}
	 * @param channelContextFilter 规则过滤器 {@link cn.starboot.socket.core.ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean bSendToUser(AioConfig aioConfig,
									  String user,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToUser(aioConfig, user, packet, channelContextFilter, true);
	}

	// --------------------------------close篇--------------------------------

	/**
	 * 关闭指定通道的连接
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 */
	public static void close(ChannelContext channelContext) {
		close(channelContext, null);
	}

	/**
	 * 关闭指定通道的连接，并提供关闭码
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
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

	/**
	 * 关闭指定业务ID的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId 业务ID
	 */
	public static void closeBsId(AioConfig aioConfig,
								 String bsId) {
		closeBsId(aioConfig, bsId, null);
	}

	/**
	 * 关闭指定业务ID的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId 业务ID
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeBsId(AioConfig aioConfig,
								 String bsId,
								 CloseCode closeCode) {
		close(getChannelContextByBsId(aioConfig, bsId), closeCode);
	}

	/**
	 * 关闭指定客户节点的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param clientNode 客户节点
	 */
	public static void closeClientNode(AioConfig aioConfig,
									   String clientNode) {
		closeClientNode(aioConfig, clientNode, null);
	}

	/**
	 * 关闭指定客户节点的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param clientNode 客户节点
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeClientNode(AioConfig aioConfig,
									   String clientNode,
									   CloseCode closeCode) {
		close(getChannelContextByClientNode(aioConfig, clientNode), closeCode);
	}

	/**
	 * 关闭指定集群ID的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId 集群ID
	 */
	public static void closeClu(AioConfig aioConfig,
								String cluId) {
		closeClu(aioConfig, cluId, null);
	}

	/**
	 * 关闭指定集群ID的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId 集群ID
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeClu(AioConfig aioConfig,
								String cluId,
								CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByCluId(aioConfig, cluId), closeCode);
	}

	/**
	 * 关闭指定群组的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId 群组
	 */
	public static void closeGroup(AioConfig aioConfig,
								  String groupId) {
		closeGroup(aioConfig, groupId, null);
	}

	/**
	 * 关闭指定群组的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId 群组
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeGroup(AioConfig aioConfig,
								  String groupId,
								  CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByGroupId(aioConfig, groupId), closeCode);
	}

	/**
	 * 关闭指定ID的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id ID
	 */
	public static void closeId(AioConfig aioConfig,
							   String id) {
		closeId(aioConfig, id, null);
	}

	/**
	 * 关闭指定ID的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id ID
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeId(AioConfig aioConfig,
							   String id,
							   CloseCode closeCode) {
		close(getChannelContextById(aioConfig, id), closeCode);
	}

	/**
	 * 关闭指定IP的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip IP
	 */
	public static void closeIp(AioConfig aioConfig,
							   String ip) {
		closeIp(aioConfig, ip, null);
	}

	/**
	 * 关闭指定IP的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip IP
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeIp(AioConfig aioConfig,
							   String ip,
							   CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByIp(aioConfig, ip), closeCode);
	}

	/**
	 * 关闭指定TOKEN的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token TOKEN
	 */
	public static void closeToken(AioConfig aioConfig,
								  String token) {
		closeToken(aioConfig, token, null);
	}

	/**
	 * 关闭指定TOKEN的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token TOKEN
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeToken(AioConfig aioConfig,
								  String token,
								  CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByToken(aioConfig, token), closeCode);
	}

	/**
	 * 关闭指定USER的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user USER
	 */
	public static void closeUser(AioConfig aioConfig,
								 String user) {
		closeUser(aioConfig, user, null);
	}

	/**
	 * 关闭指定USER的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user USER
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
	public static void closeUser(AioConfig aioConfig,
								 String user,
								 CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByUser(aioConfig, user), closeCode);
	}

	/**
	 * 关闭指定集合的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param setWithLock 带有锁结构的SET集合
	 * @param closeCode 关闭状态码 {@link cn.starboot.socket.core.CloseCode}
	 */
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

	// ---------------------------------Get篇---------------------------------

	/**
	 * 获取所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getAll(AioConfig aioConfig) {
		return aioConfig.getConnections();
	}

	/**
	 * 获取所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getAllChannelContexts(AioConfig aioConfig) {
		return getAll(aioConfig);
	}

	/**
	 * 根据业务ID获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId 业务ID
	 * @return
	 */
	public static ChannelContext getByBsId(AioConfig aioConfig,
										   String bsId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.getChannelContext(bsId);
	}

	/**
	 * 根据业务ID获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId 业务ID
	 * @return
	 */
	public static ChannelContext getChannelContextByBsId(AioConfig aioConfig,
														 String bsId) {
		return getByBsId(aioConfig, bsId);
	}

	/**
	 * 根据客户节点获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param clientNode 客户节点
	 * @return
	 */
	public static ChannelContext getByClientNode(AioConfig aioConfig,
												 String clientNode) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.getChannelContext(clientNode);
	}

	/**
	 * 根据客户节点获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param clientNode 客户节点
	 * @return
	 */
	public static ChannelContext getChannelContextByClientNode(AioConfig aioConfig,
															   String clientNode) {
		return getByClientNode(aioConfig, clientNode);
	}

	/**
	 * 根据集群ID获取指定ID下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId 集群ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getByCluId(AioConfig aioConfig,
														 String cluId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
	}

	/**
	 * 根据集群ID获取指定ID下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId 集群ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getChannelContextByCluId(AioConfig aioConfig,
																	   String cluId) {
		return getByCluId(aioConfig, cluId);
	}

	/**
	 * 根据群组ID获取指定ID下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId 群组ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getByGroupId(AioConfig aioConfig,
														   String groupId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
	}

	/**
	 * 根据群组ID获取指定ID下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId 群组ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getChannelContextByGroupId(AioConfig aioConfig,
																		 String groupId) {
		return getByGroupId(aioConfig, groupId);
	}

	/**
	 * 根据ID获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id ID
	 * @return
	 */
	public static ChannelContext getById(AioConfig aioConfig,
										 String id) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.getChannelContext(id);
	}

	/**
	 * 根据ID获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id ID
	 * @return
	 */
	public static ChannelContext getChannelContextById(AioConfig aioConfig,
													   String id) {
		return getById(aioConfig, id);
	}

	/**
	 * 根据IP获取指定IP下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip IP
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getByIp(AioConfig aioConfig,
													  String ip) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
	}

	/**
	 * 根据IP获取指定IP下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip IP
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getChannelContextByIp(AioConfig aioConfig,
																	String ip) {
		return getByIp(aioConfig, ip);
	}

	/**
	 * 根据TOKEN获取指定TOKEN下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token TOKEN
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getByToken(AioConfig aioConfig,
														 String token) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
	}

	/**
	 * 根据集群TOKEN获取指定TOKEN下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token TOKEN
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getChannelContextByToken(AioConfig aioConfig,
																	   String token) {
		return getByToken(aioConfig, token);
	}

	/**
	 * 根据USER获取指定USER下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user USER
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getByUser(AioConfig aioConfig,
														String user) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
	}

	/**
	 * 根据USER获取指定USER下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user USER
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static SetWithLock<ChannelContext> getChannelContextByUser(AioConfig aioConfig,
																	  String user) {
		return getByUser(aioConfig, user);
	}

	/**
	 * 按照分页获取所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public static Page<ChannelContext> getPageOfAll(AioConfig aioConfig,
													Integer pageIndex,
													Integer pageSize) {
		SetWithLock<ChannelContext> connections = aioConfig.getConnections();
		return getPageOfSet(connections, pageIndex, pageSize);
	}

	/**
	 * 按照分页获取指定集群组ID下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
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

	/**
	 * 获取指定集群组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId
	 * @return
	 */
	public static Integer cluCount(AioConfig aioConfig,
								   String cluId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId)
				.size();
	}

	/**
	 * 判断指定用户是否在指定集群组中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId
	 * @param channelContext
	 * @return
	 */
	public static Boolean isInClu(AioConfig aioConfig,
								  String cluId,
								  ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		return isInSet(setWithLock, channelContext);
	}

	/**
	 * 按照分页获取指定群组ID下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
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

	/**
	 * 获取指定群组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId
	 * @return
	 */
	// 群组有多少个连接
	public static Integer groupCount(AioConfig aioConfig,
									 String groupId) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId)
				.size();
	}

	/**
	 * 判断指定用户是否在指定群组中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId
	 * @param channelContext
	 * @return
	 */
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

	/**
	 *
	 * 按照分页获取指定IP组下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
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

	/**
	 * 获取指定IP组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip
	 * @return
	 */
	public static Integer ipCount(AioConfig aioConfig,
								  String ip) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip)
				.size();
	}

	/**
	 * 判断指定用户是否在指定IP组中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip
	 * @param channelContext
	 * @return
	 */
	public static Boolean isInIp(AioConfig aioConfig,
								 String ip,
								 ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		return isInSet(setWithLock, channelContext);
	}

	/**
	 * 按照分页获取指定TOKEN组下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
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

	/**
	 * 获取指定TOKEN组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token
	 * @return
	 */
	public static Integer tokenCount(AioConfig aioConfig,
									 String token) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token)
				.size();
	}

	/**
	 * 判断指定用户是否在指定TOKEN组中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token
	 * @param channelContext
	 * @return
	 */
	public static Boolean isInToken(AioConfig aioConfig,
									String token,
									ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		return isInSet(setWithLock, channelContext);
	}

	/**
	 * 按照分页获取指定USER组下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
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

	/**
	 * 获取指定USER组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user
	 * @return
	 */
	public static Integer userCount(AioConfig aioConfig,
									String user) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user)
				.size();
	}

	/**
	 * 判断指定用户是否在指定USER组中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user
	 * @param channelContext
	 * @return
	 */
	public static Boolean isInUser(AioConfig aioConfig,
								   String user,
								   ChannelContext channelContext) {
		SetWithLock<ChannelContext> setWithLock = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		return isInSet(setWithLock, channelContext);
	}

	/**
	 * 按照分页获取指定集合下所有在线用户
	 *
	 * @param setWithLock
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	private static Page<ChannelContext> getPageOfSet(SetWithLock<ChannelContext> setWithLock,
													 Integer pageIndex,
													 Integer pageSize) {
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize);
	}

	/**
	 * 判断指定用户是否在指定SET集合组中
	 *
	 * @param setWithLock
	 * @param channelContext
	 * @return
	 */
	private static Boolean isInSet(SetWithLock<ChannelContext> setWithLock,
								   ChannelContext channelContext) {
		AtomicBoolean contains = new AtomicBoolean(false);
		setWithLock.handle((ReadLockHandler<Set<ChannelContext>>)
				channelContextSet -> contains.set(channelContextSet.contains(channelContext)));
		return contains.get();
	}

	// -------------------------------Remove篇--------------------------------

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

	// --------------------------------Send篇---------------------------------

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

	// -------------------------------unBing篇--------------------------------

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
