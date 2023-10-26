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

import cn.starboot.socket.core.enums.CloseCode;
import cn.starboot.socket.core.enums.MaintainEnum;
import cn.starboot.socket.core.utils.concurrent.collection.ConcurrentWithSet;
import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.core.utils.page.Page;
import cn.starboot.socket.core.utils.page.PageUtils;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * aio-socket基础常用API
 * 绑定、绑定
 * 单发（同步发送，异步发送）
 * 群发
 * 移除连接、关闭连接
 * GET：获取指定群组或Id的ChannelContext or ChannelContexts
 * <p>
 * 使用方式：Aio.bindBsId(bsId, channelContext);
 * 如果需要返回结果：Boolean b = Aio.bindBsId(bsId, channelContext);
 *
 * @author MDong
 * @author t-io
 * @version 2.10.1.v20211002-RELEASE
 */
public class Aio {

	private static final Logger LOGGER = LoggerFactory.getLogger(Aio.class);

	// ---------------------------------绑定篇--------------------------------

	/**
	 * 绑定业务ID
	 *
	 * @param bsId           业务ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return {@code true} 绑定成功 或者
	 * {@code false} 绑定失败
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
	 * 绑定客户端节点
	 *
	 * @param node           客户端 {@link Node}
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 绑定状态
	 */
	public static Boolean bindCliNode(Node node,
									  ChannelContext channelContext) {
		if (Objects.isNull(node))
			return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.join(node.getAddr(), channelContext);
	}

	/**
	 * 绑定集群ID
	 *
	 * @param cluId          集群ID
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
	 * @param groupId        群组ID
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
	 * @param userId    用户ID
	 * @param groupId   群组ID
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
	 * @param id             ID
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
	 * @param ip             IP
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
	 * @param token          TOKEN
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
	 * @param user           USER
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
	 * @param packet         数据报文 {@link Packet}
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
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToAll(AioConfig aioConfig,
									 Packet packet) {
		return bSendToAll(aioConfig, packet, null);
	}

	/**
	 * 同步发送到平台所有用户，并且带有过滤规则
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
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
	 * @param bsId      业务ID
	 * @param packet    数据报文 {@link Packet}
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
	 * @param node      客户端 {@link Node}
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean bSendToClientNode(AioConfig aioConfig,
											Node node,
											Packet packet) {
		return sendToClientNode(aioConfig, node, packet, true);
	}

	/**
	 * 同步发送到指定集群中
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群ID
	 * @param packet    数据报文 {@link Packet}
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
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId                集群ID
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
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
	 * @param groupId   群组ID
	 * @param packet    数据报文 {@link Packet}
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
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId              群组ID
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
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
	 * @param id        ID
	 * @param packet    数据报文 {@link Packet}
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
	 * @param ip        IP
	 * @param packet    数据报文 {@link Packet}
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
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip                   IP
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
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
	 * @param token     TOKEN
	 * @param packet    数据报文 {@link Packet}
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
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token                TOKEN
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
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
	 * @param user      USER
	 * @param packet    数据报文 {@link Packet}
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
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user                 USER
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
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
	 * @param closeCode      关闭状态码 {@link CloseCode}
	 */
	public static void close(ChannelContext channelContext,
							 CloseCode closeCode) {
		if (Objects.isNull(channelContext)) {
			return;
		}
		// 从各个关系中移除
		Boolean aBoolean = unbindFromAll(channelContext);
		// 停止各种处理器的运行
//		channelContext.getDecodeTaskRunnable().setCanceled(true);
//		channelContext.getHandlerTaskRunnable().setCanceled(true);
//		channelContext.getSendTaskRunnable().setCanceled(true);
		channelContext.getAioWorker().setCanceled(true);
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
	 * @param bsId      业务ID
	 */
	public static void closeBsId(AioConfig aioConfig,
								 String bsId) {
		closeBsId(aioConfig, bsId, null);
	}

	/**
	 * 关闭指定业务ID的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId      业务ID
	 * @param closeCode 关闭状态码 {@link CloseCode}
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
	 * @param node      客户端 {@link Node}
	 */
	public static void closeClientNode(AioConfig aioConfig,
									   Node node) {
		closeClientNode(aioConfig, node, null);
	}

	/**
	 * 关闭指定客户节点的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void closeClientNode(AioConfig aioConfig,
									   Node node,
									   CloseCode closeCode) {
		close(getChannelContextByClientNode(aioConfig, node), closeCode);
	}

	/**
	 * 关闭指定集群ID的连接
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群ID
	 */
	public static void closeClu(AioConfig aioConfig,
								String cluId) {
		closeClu(aioConfig, cluId, null);
	}

	/**
	 * 关闭指定集群ID的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群ID
	 * @param closeCode 关闭状态码 {@link CloseCode}
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
	 * @param groupId   群组
	 */
	public static void closeGroup(AioConfig aioConfig,
								  String groupId) {
		closeGroup(aioConfig, groupId, null);
	}

	/**
	 * 关闭指定群组的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId   群组
	 * @param closeCode 关闭状态码 {@link CloseCode}
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
	 * @param id        ID
	 */
	public static void closeId(AioConfig aioConfig,
							   String id) {
		closeId(aioConfig, id, null);
	}

	/**
	 * 关闭指定ID的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id        ID
	 * @param closeCode 关闭状态码 {@link CloseCode}
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
	 * @param ip        IP
	 */
	public static void closeIp(AioConfig aioConfig,
							   String ip) {
		closeIp(aioConfig, ip, null);
	}

	/**
	 * 关闭指定IP的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip        IP
	 * @param closeCode 关闭状态码 {@link CloseCode}
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
	 * @param token     TOKEN
	 */
	public static void closeToken(AioConfig aioConfig,
								  String token) {
		closeToken(aioConfig, token, null);
	}

	/**
	 * 关闭指定TOKEN的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token     TOKEN
	 * @param closeCode 关闭状态码 {@link CloseCode}
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
	 * @param user      USER
	 */
	public static void closeUser(AioConfig aioConfig,
								 String user) {
		closeUser(aioConfig, user, null);
	}

	/**
	 * 关闭指定USER的连接，并提供关闭码
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user      USER
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void closeUser(AioConfig aioConfig,
								 String user,
								 CloseCode closeCode) {
		closeSet(aioConfig, getChannelContextByUser(aioConfig, user), closeCode);
	}

	/**
	 * 关闭指定集合的连接，并提供关闭码
	 *
	 * @param aioConfig         配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param concurrentWithSet 带有锁结构的SET集合
	 * @param closeCode         关闭状态码 {@link CloseCode}
	 */
	public static void closeSet(AioConfig aioConfig,
								ConcurrentWithSet<ChannelContext> concurrentWithSet,
								CloseCode closeCode) {
		if (Objects.isNull(concurrentWithSet) || concurrentWithSet.isEmpty()) {
			return;
		}
		concurrentWithSet.handle(new ConcurrentWithReadHandler<Set<ChannelContext>>() {
			@Override
			public void handler(Set<ChannelContext> channelContextSet) throws Exception {
				channelContextSet.forEach(new Consumer<ChannelContext>() {
					@Override
					public void accept(ChannelContext channelContext) {
						if (Objects.nonNull(channelContext)) {
							close(channelContext, closeCode);
						}
					}
				});
			}
		});
	}

	// ---------------------------------Get篇---------------------------------

	/**
	 * 获取所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getAll(AioConfig aioConfig) {
		return aioConfig.getConnections();
	}

	/**
	 * 获取所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getAllChannelContexts(AioConfig aioConfig) {
		return getAll(aioConfig);
	}

	/**
	 * 根据业务ID获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId      业务ID
	 * @return 用户上下文信息
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
	 * @param bsId      业务ID
	 * @return 用户上下文信息
	 */
	public static ChannelContext getChannelContextByBsId(AioConfig aioConfig,
														 String bsId) {
		return getByBsId(aioConfig, bsId);
	}

	/**
	 * 根据客户节点获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 * @return 用户上下文信息
	 */
	public static ChannelContext getByClientNode(AioConfig aioConfig,
												 Node node) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.getChannelContext(node.getAddr());
	}

	/**
	 * 根据客户节点获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 * @return 用户上下文信息
	 */
	public static ChannelContext getChannelContextByClientNode(AioConfig aioConfig,
															   Node node) {
		return getByClientNode(aioConfig, node);
	}

	/**
	 * 根据集群ID获取指定ID下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getByCluId(AioConfig aioConfig,
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
	 * @param cluId     集群ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getChannelContextByCluId(AioConfig aioConfig,
																			 String cluId) {
		return getByCluId(aioConfig, cluId);
	}

	/**
	 * 根据群组ID获取指定ID下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId   群组ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getByGroupId(AioConfig aioConfig,
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
	 * @param groupId   群组ID
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getChannelContextByGroupId(AioConfig aioConfig,
																			   String groupId) {
		return getByGroupId(aioConfig, groupId);
	}

	/**
	 * 根据ID获取指定用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id        ID
	 * @return 用户上下文信息
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
	 * @param id        ID
	 * @return 用户上下文信息
	 */
	public static ChannelContext getChannelContextById(AioConfig aioConfig,
													   String id) {
		return getById(aioConfig, id);
	}

	/**
	 * 根据IP获取指定IP下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip        IP
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getByIp(AioConfig aioConfig,
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
	 * @param ip        IP
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getChannelContextByIp(AioConfig aioConfig,
																		  String ip) {
		return getByIp(aioConfig, ip);
	}

	/**
	 * 根据TOKEN获取指定TOKEN下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token     TOKEN
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getByToken(AioConfig aioConfig,
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
	 * @param token     TOKEN
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getChannelContextByToken(AioConfig aioConfig,
																			 String token) {
		return getByToken(aioConfig, token);
	}

	/**
	 * 根据USER获取指定USER下所有在线用户上下文信息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user      USER
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getByUser(AioConfig aioConfig,
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
	 * @param user      USER
	 * @return 用户上下文信息带锁数据结构的SET集合
	 */
	public static ConcurrentWithSet<ChannelContext> getChannelContextByUser(AioConfig aioConfig,
																			String user) {
		return getByUser(aioConfig, user);
	}

	/**
	 * 按照分页获取所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param pageIndex 索引页
	 * @param pageSize  页面大小
	 * @return 分页结果
	 */
	public static Page<ChannelContext> getPageOfAll(AioConfig aioConfig,
													Integer pageIndex,
													Integer pageSize) {
		ConcurrentWithSet<ChannelContext> connections = aioConfig.getConnections();
		return getPageOfSet(connections, pageIndex, pageSize);
	}

	/**
	 * 按照分页获取指定集群组ID下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群ID
	 * @param pageIndex 索引页
	 * @param pageSize  页面大小
	 * @return 分页结果
	 */
	public static Page<ChannelContext> getPageOfClu(AioConfig aioConfig,
													String cluId,
													Integer pageIndex,
													Integer pageSize) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		return getPageOfSet(concurrentWithSet, pageIndex, pageSize);
	}

	/**
	 * 获取指定集群组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群ID
	 * @return int
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
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId          集群ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return boolean
	 */
	public static Boolean isInClu(AioConfig aioConfig,
								  String cluId,
								  ChannelContext channelContext) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		return isInSet(concurrentWithSet, channelContext);
	}

	/**
	 * 按照分页获取指定群组ID下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId   群组ID
	 * @param pageIndex 索引页
	 * @param pageSize  页面大小
	 * @return 分页结果
	 */
	public static Page<ChannelContext> getPageOfGroup(AioConfig aioConfig,
													  String groupId,
													  Integer pageIndex,
													  Integer pageSize) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
		return getPageOfSet(concurrentWithSet, pageIndex, pageSize);
	}

	/**
	 * 获取指定群组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId   群组ID
	 * @return int
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
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId        群组ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return boolean
	 */
	// 某通道是否在某群组中
	public static Boolean isInGroup(AioConfig aioConfig,
									String groupId,
									ChannelContext channelContext) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
		return isInSet(concurrentWithSet, channelContext);
	}

	/**
	 * 按照分页获取指定IP组下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip        IP
	 * @param pageIndex 索引页
	 * @param pageSize  页面大小
	 * @return 分页结果
	 */
	public static Page<ChannelContext> getPageOfIp(AioConfig aioConfig,
												   String ip,
												   Integer pageIndex,
												   Integer pageSize) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		return getPageOfSet(concurrentWithSet, pageIndex, pageSize);
	}

	/**
	 * 获取指定IP组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip        IP
	 * @return int
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
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip             IP
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return boolean
	 */
	public static Boolean isInIp(AioConfig aioConfig,
								 String ip,
								 ChannelContext channelContext) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		return isInSet(concurrentWithSet, channelContext);
	}

	/**
	 * 按照分页获取指定TOKEN组下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token     TOKEN
	 * @param pageIndex 索引页
	 * @param pageSize  页面大小
	 * @return 分页结果
	 */
	public static Page<ChannelContext> getPageOfToken(AioConfig aioConfig,
													  String token,
													  Integer pageIndex,
													  Integer pageSize) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		return getPageOfSet(concurrentWithSet, pageIndex, pageSize);
	}

	/**
	 * 获取指定TOKEN组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token     TOKEN
	 * @return int
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
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token          TOKEN
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return boolean
	 */
	public static Boolean isInToken(AioConfig aioConfig,
									String token,
									ChannelContext channelContext) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		return isInSet(concurrentWithSet, channelContext);
	}

	/**
	 * 按照分页获取指定USER组下所有在线用户
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user      USER
	 * @param pageIndex 索引页
	 * @param pageSize  页面大小
	 * @return 分页结果
	 */
	public static Page<ChannelContext> getPageOfUser(AioConfig aioConfig,
													 String user,
													 Integer pageIndex,
													 Integer pageSize) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		return getPageOfSet(concurrentWithSet, pageIndex, pageSize);
	}

	/**
	 * 获取指定USER组下在线用户个数
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user      USER
	 * @return int
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
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user           USER
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return boolean
	 */
	public static Boolean isInUser(AioConfig aioConfig,
								   String user,
								   ChannelContext channelContext) {
		ConcurrentWithSet<ChannelContext> concurrentWithSet = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		return isInSet(concurrentWithSet, channelContext);
	}

	/**
	 * 按照分页获取指定集合下所有在线用户
	 *
	 * @param concurrentWithSet 带有锁结构的SET集合
	 * @param pageIndex         索引页
	 * @param pageSize          页面大小
	 * @return 分页结果
	 */
	private static Page<ChannelContext> getPageOfSet(ConcurrentWithSet<ChannelContext> concurrentWithSet,
													 Integer pageIndex,
													 Integer pageSize) {
		return PageUtils.fromSetWithLock(concurrentWithSet, pageIndex, pageSize);
	}

	/**
	 * 判断指定用户是否在指定SET集合组中
	 *
	 * @param concurrentWithSet 带有锁结构的SET集合
	 * @param channelContext    用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return boolean
	 */
	private static Boolean isInSet(ConcurrentWithSet<ChannelContext> concurrentWithSet,
								   ChannelContext channelContext) {
		return concurrentWithSet.contains(channelContext);
	}

	// -------------------------------Remove篇--------------------------------

	/**
	 * 移除连接(与关闭一个道理)
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 */
	public static void remove(ChannelContext channelContext) {
		remove(channelContext, null);
	}

	/**
	 * 移除连接(与关闭一个道理)
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @param closeCode      关闭状态码 {@link CloseCode}
	 */
	public static void remove(ChannelContext channelContext,
							  CloseCode closeCode) {
		close(channelContext, closeCode);
	}

	/**
	 * 根据业务ID移除连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId      业务ID
	 */
	public static void removeBsId(AioConfig aioConfig,
								  String bsId) {
		removeBsId(aioConfig, bsId, null);
	}

	/**
	 * 根据业务ID移除连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId      业务ID
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeBsId(AioConfig aioConfig,
								  String bsId,
								  CloseCode closeCode) {
		remove(getChannelContextByBsId(aioConfig, bsId), closeCode);
	}

	/**
	 * 根据客户节点移除连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 */
	public static void removeClientNode(AioConfig aioConfig,
										Node node) {
		removeClientNode(aioConfig, node, null);
	}

	/**
	 * 根据客户节点移除连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeClientNode(AioConfig aioConfig,
										Node node,
										CloseCode closeCode) {
		remove(getChannelContextByClientNode(aioConfig, node), closeCode);
	}

	/**
	 * 根据集群组ID移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群组ID
	 */
	public static void removeClu(AioConfig aioConfig,
								 String cluId) {
		removeClu(aioConfig, cluId, null);
	}

	/**
	 * 根据集群组ID移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群组ID
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeClu(AioConfig aioConfig,
								 String cluId,
								 CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByCluId(aioConfig, cluId), closeCode);
	}

	/**
	 * 根据群组ID移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId   群组ID
	 */
	public static void removeGroup(AioConfig aioConfig,
								   String groupId) {
		removeGroup(aioConfig, groupId, null);
	}

	/**
	 * 根据群组ID移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId   群组ID
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeGroup(AioConfig aioConfig,
								   String groupId,
								   CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByGroupId(aioConfig, groupId), closeCode);
	}

	/**
	 * 根据ID移除连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id        ID
	 */
	public static void removeId(AioConfig aioConfig,
								String id) {
		removeId(aioConfig, id, null);
	}

	/**
	 * 根据ID移除连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id        ID
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeId(AioConfig aioConfig,
								String id,
								CloseCode closeCode) {
		remove(getChannelContextById(aioConfig, id), closeCode);
	}

	/**
	 * 根据IP移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip        IP
	 */
	public static void removeIp(AioConfig aioConfig,
								String ip) {
		removeIp(aioConfig, ip, null);
	}

	/**
	 * 根据IP移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip        IP
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeIp(AioConfig aioConfig,
								String ip,
								CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByIp(aioConfig, ip), closeCode);
	}

	/**
	 * 根据TOKEN移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token     TOKEN
	 */
	public static void removeToken(AioConfig aioConfig,
								   String token) {
		removeToken(aioConfig, token, null);
	}

	/**
	 * 根据TOKEN移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token     TOKEN
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeToken(AioConfig aioConfig,
								   String token,
								   CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByToken(aioConfig, token), closeCode);
	}

	/**
	 * 根据USER移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user      USER
	 */
	public static void removeUser(AioConfig aioConfig,
								  String user) {
		removeUser(aioConfig, user, null);
	}

	/**
	 * 根据USER移除组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user      USER
	 * @param closeCode 关闭状态码 {@link CloseCode}
	 */
	public static void removeUser(AioConfig aioConfig,
								  String user,
								  CloseCode closeCode) {
		removeSet(aioConfig, getChannelContextByUser(aioConfig, user), closeCode);
	}

	/**
	 * 移除集合SET组内所有连接(与关闭一个道理)
	 *
	 * @param aioConfig         配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param concurrentWithSet 带有锁结构的SET集合
	 * @param closeCode         关闭状态码 {@link CloseCode}
	 */
	public static void removeSet(AioConfig aioConfig,
								 ConcurrentWithSet<ChannelContext> concurrentWithSet,
								 CloseCode closeCode) {
		closeSet(aioConfig, concurrentWithSet, closeCode);
	}

	// ------------------------------异步发送篇-------------------------------

	/**
	 * 异步发送/同步发送 (使用同步发送时，在确保开启ACKPlugin后，只需要将Packet中Req字段赋值即可)
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @param packet         数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean send(ChannelContext channelContext,
							   Packet packet) {
		return send0(channelContext, packet, false);
	}

	/**
	 * 发送内部逻辑，不对外暴露接口
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @param packet         数据报文 {@link Packet}
	 * @param isBlock        是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean send0(ChannelContext channelContext,
								 Packet packet,
								 boolean isBlock) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		return channelContext.aioEncoder(packet, isBlock, true);
	}

	/**
	 * 向所有在线用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToAll(AioConfig aioConfig,
									Packet packet) {
		return sendToAll(aioConfig, packet, null);
	}

	/**
	 * 向所有在线用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean sendToAll(AioConfig aioConfig,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToAll(aioConfig, packet, channelContextFilter, false);
	}

	/**
	 * 向所有在线用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @param isBlock              是否采用同步等待发送
	 * @return 发送状态
	 */
	public static Boolean sendToAll(AioConfig aioConfig,
									Packet packet,
									ChannelContextFilter channelContextFilter,
									boolean isBlock) {
		if (aioConfig.isUseConnections()) {
			if (aioConfig.getConnections().size() > 0) {
				sendToSet(aioConfig, aioConfig.getConnections(), packet, channelContextFilter, isBlock);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("当前aio-socket服务器没有连接在线");
				}
			}
			return true;
		} else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("未开启保持连接状态");
			}
			return false;
		}
	}

	/**
	 * 向指定业务ID用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId      业务ID
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToBsId(AioConfig aioConfig,
									 String bsId,
									 Packet packet) {
		return sendToBsId(aioConfig, bsId, packet, false);
	}

	/**
	 * 向指定业务ID用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId      业务ID
	 * @param packet    数据报文 {@link Packet}
	 * @param isBlock   是否采用同步等待发送
	 * @return 发送状态
	 */
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

	/**
	 * 向指定客户节点用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToClientNode(AioConfig aioConfig,
										   Node node,
										   Packet packet) {
		return sendToClientNode(aioConfig, node, packet, false);
	}

	/**
	 * 向指定客户节点用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 * @param packet    数据报文 {@link Packet}
	 * @param isBlock   是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean sendToClientNode(AioConfig aioConfig,
											Node node,
											Packet packet,
											boolean isBlock) {
		ChannelContext channelContext = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.getChannelContext(node.getAddr());
		return send0(channelContext, packet, isBlock);
	}

	/**
	 * 向指定集群ID所有用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId     集群ID
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToCluId(AioConfig aioConfig,
									  String cluId,
									  Packet packet) {
		return sendToCluId(aioConfig, cluId, packet, null);
	}

	/**
	 * 向指定集群ID所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId                集群ID
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean sendToCluId(AioConfig aioConfig,
									  String cluId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToCluId(aioConfig, cluId, packet, channelContextFilter, false);
	}

	/**
	 * 向指定集群ID所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId                集群ID
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @param isBlock              是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean sendToCluId(AioConfig aioConfig,
									   String cluId,
									   Packet packet,
									   ChannelContextFilter channelContextFilter,
									   boolean isBlock) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("集群Id:{},没有绑定任何通道", cluId);
			}
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	/**
	 * 向指定群ID内所有用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId   群ID
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet) {
		return sendToGroup(aioConfig, groupId, packet, null);
	}

	/**
	 * 向指定群ID内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId              群ID
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToGroup(aioConfig, groupId, packet, channelContextFilter, false);
	}

	/**
	 * 向指定群ID内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId              群ID
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @param isBlock              是否采用同步等待发送
	 * @return 发送状态
	 */
	public static Boolean sendToGroup(AioConfig aioConfig,
									  String groupId,
									  Packet packet,
									  ChannelContextFilter channelContextFilter,
									  boolean isBlock) {
		// 群组成员集合
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("群组Id:{},没有绑定任何通道", groupId);
			}
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	/**
	 * 向指定ID用户发送消息
	 *
	 * @param config 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id     ID
	 * @param packet 数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToId(AioConfig config,
								   String id,
								   Packet packet) {
		return sendToId(config, id, packet, false);
	}

	/**
	 * 向指定ID用户发送消息
	 *
	 * @param config  配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id      ID
	 * @param packet  数据报文 {@link Packet}
	 * @param isBlock 是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean sendToId(AioConfig config,
									String id,
									Packet packet,
									boolean isBlock) {
		return send0(getChannelContextById(config, id), packet, isBlock);
	}

	/**
	 * 向指定IP内所有用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip        IP
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet) {
		return sendToIp(aioConfig, ip, packet, null);
	}

	/**
	 * 向指定IP内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip                   IP
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean sendToIp(AioConfig aioConfig,
								   String ip,
								   Packet packet,
								   ChannelContextFilter channelContextFilter) {
		return sendToIp(aioConfig, ip, packet, channelContextFilter, false);
	}

	/**
	 * 向指定IP内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip                   IP
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @param isBlock              是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean sendToIp(AioConfig aioConfig,
									String ip,
									Packet packet,
									ChannelContextFilter channelContextFilter,
									boolean isBlock) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ip:{},没有绑定任何通道", ip);
			}
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	/**
	 * 向指定SET集合中所有用户发送消息
	 *
	 * @param aioConfig         配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param concurrentWithSet 带有锁结构的SET集合
	 * @param packet            数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToSet(AioConfig aioConfig,
									ConcurrentWithSet<ChannelContext> concurrentWithSet,
									Packet packet) {
		return sendToSet(aioConfig, concurrentWithSet, packet, null);
	}

	/**
	 * 向指定SET集合中所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param concurrentWithSet    带有锁结构的SET集合
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean sendToSet(AioConfig aioConfig,
									ConcurrentWithSet<ChannelContext> concurrentWithSet,
									Packet packet,
									ChannelContextFilter channelContextFilter) {
		return sendToSet(aioConfig, concurrentWithSet, packet, channelContextFilter, false);
	}

	/**
	 * 向指定SET集合中所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param concurrentWithSet    带有锁结构的SET集合
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @param isBlock              是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean sendToSet(AioConfig aioConfig,
									 ConcurrentWithSet<ChannelContext> concurrentWithSet,
									 Packet packet,
									 ChannelContextFilter channelContextFilter,
									 boolean isBlock) {
		if (Objects.isNull(concurrentWithSet) || concurrentWithSet.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("集合内没人在线");
			}
			return false;
		}
		LongAdder sendNum = new LongAdder();
		LongAdder sendSuc = new LongAdder();
		concurrentWithSet
				.handle((ConcurrentWithReadHandler<Set<ChannelContext>>)
						channelContextSet ->
								channelContextSet.forEach(channelContext -> {
									if (Objects.nonNull(channelContext)
											&& (Objects.isNull(channelContextFilter)
											|| channelContextFilter.filter(channelContext))) {
										sendNum.increment();
										if (send0(channelContext, packet, isBlock)) {
											sendSuc.increment();
										}
									}
								}));

//		if (Objects.isNull(channelContextFilter)) {
//			concurrentWithSet
//					.handle((ReadLockHandler<Set<ChannelContext>>)
//							channelContextSet -> channelContextSet.forEach(
//									channelContext -> {
//										if (Objects.nonNull(channelContext)) {
//											sendNum.increment();
//											if (send0(channelContext, packet, isBlock)) {
//												sendSuc.increment();
//											}
//										}
//									}));
//		} else {
//			concurrentWithSet
//					.handle((ReadLockHandler<Set<ChannelContext>>)
//							channelContextSet -> channelContextSet.forEach(
//									channelContext -> {
//										if (Objects.nonNull(channelContext)
//												&& channelContextFilter.filter(channelContext)) {
//											sendNum.increment();
//											if (send0(channelContext, packet, isBlock)) {
//												sendSuc.increment();
//											}
//										}
//									}));
//		}
		return sendNum.longValue() == sendSuc.longValue();
	}

	/**
	 * 向指定TOKEN内所有用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token     TOKEN
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToToken(AioConfig aioConfig,
									  String token,
									  Packet packet) {
		return sendToToken(aioConfig, token, packet, null);
	}

	/**
	 * 向指定TOKEN内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token                TOKEN
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean sendToToken(AioConfig aioConfig,
									  String token,
									  Packet packet,
									  ChannelContextFilter channelContextFilter) {
		return sendToToken(aioConfig, token, packet, channelContextFilter, false);
	}

	/**
	 * 向指定TOKEN内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token                TOKEN
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @param isBlock              是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean sendToToken(AioConfig aioConfig,
									   String token,
									   Packet packet,
									   ChannelContextFilter channelContextFilter,
									   boolean isBlock) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("token:{},没有绑定任何通道", token);
			}
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	/**
	 * 向指定USER内所有用户发送消息
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user      USER
	 * @param packet    数据报文 {@link Packet}
	 * @return 发送状态
	 */
	public static Boolean sendToUser(AioConfig aioConfig,
									 String user,
									 Packet packet) {
		return sendToUser(aioConfig, user, packet, null);
	}

	/**
	 * 向指定USER内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user                 USER
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean sendToUser(AioConfig aioConfig,
									 String user,
									 Packet packet,
									 ChannelContextFilter channelContextFilter) {
		return sendToUser(aioConfig, user, packet, channelContextFilter, false);
	}

	/**
	 * 向指定USER内所有用户发送消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user                 USER
	 * @param packet               数据报文 {@link Packet}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @param isBlock              是否采用同步等待发送
	 * @return 发送状态
	 */
	private static Boolean sendToUser(AioConfig aioConfig,
									  String user,
									  Packet packet,
									  ChannelContextFilter channelContextFilter,
									  boolean isBlock) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("user:{},没有绑定任何通道", user);
			}
			return false;
		}
		return sendToSet(aioConfig, set, packet, channelContextFilter, isBlock);
	}

	// -------------------------------多包发送篇--------------------------------

	/**
	 * 多包发送适配器对象
	 * ~~
	 * 为了对外暴露统一API
	 * 因此将ChannelContext对象的发送方法设置为了protected
	 * 通过适配器设计模式实现多包发送，提升连续发送效率
	 * ~~
	 *
	 * @author MDong
	 */
	public static class OutputChannelContext {

		private final ChannelContext channelContext;

		public OutputChannelContext(ChannelContext channelContext) {
			this.channelContext = channelContext;
		}

		public void write(Packet packet) {
			channelContext.aioEncoder(packet, false, false);
		}
	}

	/**
	 * 多包发送过程
	 * 1.多包发送不支持阻塞发送
	 * 2.多个包组成的整体支持阻塞发送
	 *
	 * @param channelContext               通道上下文
	 * @param outPutChannelContextConsumer 多包发送消费器
	 */
	public static boolean multiSend(ChannelContext channelContext,
									Consumer<OutputChannelContext> outPutChannelContextConsumer) {
		if (Objects.isNull(channelContext)) {
			return false;
		}
		outPutChannelContextConsumer.accept(new OutputChannelContext(channelContext));
		channelContext.flush(false);
		return true;
	}

	/**
	 * 向所有在线用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToAll(AioConfig aioConfig,
										 Consumer<OutputChannelContext> outPutConsumer) {
		return multiSendToAll(aioConfig, outPutConsumer, null);
	}

	/**
	 * 向所有在线用户发送多包消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param outPutConsumer       数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean multiSendToAll(AioConfig aioConfig,
										 Consumer<OutputChannelContext> outPutConsumer,
										 ChannelContextFilter channelContextFilter) {
		if (aioConfig.isUseConnections()) {
			if (aioConfig.getConnections().size() > 0) {
				multiSendToSet(aioConfig, aioConfig.getConnections(), outPutConsumer, channelContextFilter);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("当前aio-socket服务器没有连接在线");
				}
			}
			return true;
		} else {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("未开启保持连接状态");
			}
			return false;
		}
	}

	/**
	 * 向指定业务ID用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId           业务ID
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToBsId(AioConfig aioConfig,
										  String bsId,
										  Consumer<OutputChannelContext> outPutConsumer) {
		ChannelContext channelContext = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.getChannelContext(bsId);
		return multiSend(channelContext, outPutConsumer);
	}

	/**
	 * 向指定客户节点用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node           客户端 {@link Node}
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToClientNode(AioConfig aioConfig,
												Node node,
												Consumer<OutputChannelContext> outPutConsumer) {
		ChannelContext channelContext = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.getChannelContext(node.getAddr());
		return multiSend(channelContext, outPutConsumer);
	}

	/**
	 * 向指定集群ID所有用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId          集群ID
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToCluId(AioConfig aioConfig,
										   String cluId,
										   Consumer<OutputChannelContext> outPutConsumer) {
		return multiSendToCluId(aioConfig, cluId, outPutConsumer, null);
	}

	/**
	 * 向指定集群ID所有用户发送多包消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param cluId                集群ID
	 * @param outPutConsumer       数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean multiSendToCluId(AioConfig aioConfig,
										   String cluId,
										   Consumer<OutputChannelContext> outPutConsumer,
										   ChannelContextFilter channelContextFilter) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.getSet(cluId);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("集群Id:{},没有绑定任何通道", cluId);
			}
			return false;
		}
		return multiSendToSet(aioConfig, set, outPutConsumer, channelContextFilter);
	}

	/**
	 * 向指定群ID内所有用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId        群ID
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToGroup(AioConfig aioConfig,
										   String groupId,
										   Consumer<OutputChannelContext> outPutConsumer) {
		return multiSendToGroup(aioConfig, groupId, outPutConsumer, null);
	}

	/**
	 * 向指定群ID内所有用户发送多包消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param groupId              群ID
	 * @param outPutConsumer       数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean multiSendToGroup(AioConfig aioConfig,
										   String groupId,
										   Consumer<OutputChannelContext> outPutConsumer,
										   ChannelContextFilter channelContextFilter) {
		// 群组成员集合
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.getSet(groupId);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("群组Id:{},没有绑定任何通道", groupId);
			}
			return false;
		}
		return multiSendToSet(aioConfig, set, outPutConsumer, channelContextFilter);
	}

	/**
	 * 向指定ID用户发送多包消息
	 *
	 * @param config         配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id             ID
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToId(AioConfig config,
										String id,
										Consumer<OutputChannelContext> outPutConsumer) {
		return multiSend(getChannelContextById(config, id), outPutConsumer);
	}

	/**
	 * 向指定IP内所有用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip             IP
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToIp(AioConfig aioConfig,
										String ip,
										Consumer<OutputChannelContext> outPutConsumer) {
		return multiSendToIp(aioConfig, ip, outPutConsumer, null);
	}

	/**
	 * 向指定IP内所有用户发送多包消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param ip                   IP
	 * @param outPutConsumer       数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean multiSendToIp(AioConfig aioConfig,
										String ip,
										Consumer<OutputChannelContext> outPutConsumer,
										ChannelContextFilter channelContextFilter) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.getSet(ip);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ip:{},没有绑定任何通道", ip);
			}
			return false;
		}
		return multiSendToSet(aioConfig, set, outPutConsumer, channelContextFilter);
	}

	/**
	 * 向指定SET集合中所有用户发送多包消息
	 *
	 * @param aioConfig         配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param concurrentWithSet 带有锁结构的SET集合
	 * @param outPutConsumer    数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToSet(AioConfig aioConfig,
										 ConcurrentWithSet<ChannelContext> concurrentWithSet,
										 Consumer<OutputChannelContext> outPutConsumer) {
		return multiSendToSet(aioConfig, concurrentWithSet, outPutConsumer, null);
	}

	/**
	 * 向指定SET集合中所有用户发送多包消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param concurrentWithSet    带有锁结构的SET集合
	 * @param outPutConsumer       数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean multiSendToSet(AioConfig aioConfig,
										 ConcurrentWithSet<ChannelContext> concurrentWithSet,
										 Consumer<OutputChannelContext> outPutConsumer,
										 ChannelContextFilter channelContextFilter) {
		if (Objects.isNull(concurrentWithSet) || concurrentWithSet.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("集合内没人在线");
			}
			return false;
		}
		LongAdder sendNum = new LongAdder();
		LongAdder sendSuc = new LongAdder();
		concurrentWithSet
				.handle((ConcurrentWithReadHandler<Set<ChannelContext>>)
						channelContextSet ->
								channelContextSet.forEach(channelContext -> {
									if (Objects.nonNull(channelContext)
											&& (Objects.isNull(channelContextFilter)
											|| channelContextFilter.filter(channelContext))) {
										sendNum.increment();
										if (multiSend(channelContext, outPutConsumer)) {
											sendSuc.increment();
										}
									}
								}));
		return sendNum.longValue() == sendSuc.longValue();
	}


	/**
	 * 向指定TOKEN内所有用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token          TOKEN
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToToken(AioConfig aioConfig,
										   String token,
										   Consumer<OutputChannelContext> outPutConsumer) {
		return multiSendToToken(aioConfig, token, outPutConsumer, null);
	}

	/**
	 * 向指定TOKEN内所有用户发送多包消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param token                TOKEN
	 * @param outPutConsumer       数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean multiSendToToken(AioConfig aioConfig,
										   String token,
										   Consumer<OutputChannelContext> outPutConsumer,
										   ChannelContextFilter channelContextFilter) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.getSet(token);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("token:{},没有绑定任何通道", token);
			}
			return false;
		}
		return multiSendToSet(aioConfig, set, outPutConsumer, channelContextFilter);
	}

	/**
	 * 向指定USER内所有用户发送多包消息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user           USER
	 * @param outPutConsumer 数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @return 发送状态
	 */
	public static Boolean multiSendToUser(AioConfig aioConfig,
										  String user,
										  Consumer<OutputChannelContext> outPutConsumer) {
		return multiSendToUser(aioConfig, user, outPutConsumer, null);
	}

	/**
	 * 向指定USER内所有用户发送多包消息
	 *
	 * @param aioConfig            配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param user                 USER
	 * @param outPutConsumer       数据报文 {@link cn.starboot.socket.core.Aio.OutputChannelContext}
	 * @param channelContextFilter 规则过滤器 {@link ChannelContextFilter}
	 *                             如果规则过滤器返回为true，则代表满足规则保留且发送
	 *                             如果为满足规则过滤器的则被抛弃，不予以处理（发送）
	 * @return 发送状态
	 */
	public static Boolean multiSendToUser(AioConfig aioConfig,
										  String user,
										  Consumer<OutputChannelContext> outPutConsumer,
										  ChannelContextFilter channelContextFilter) {
		ConcurrentWithSet<ChannelContext> set = aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.getSet(user);
		if (Objects.isNull(set)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("user:{},没有绑定任何通道", user);
			}
			return false;
		}
		return multiSendToSet(aioConfig, set, outPutConsumer, channelContextFilter);
	}


	// -------------------------------unBing篇--------------------------------

	/**
	 * 将指定用户的上下文信息从目前系统所存在的所有关系中解除
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
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

	/**
	 * 解绑指定用户的业务ID，且不提供业务ID
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindBsId(ChannelContext channelContext) {
		return unbindBsId(channelContext.getAioConfig(), null, channelContext);
	}

	/**
	 * 解绑指定用户的业务ID，提供业务ID
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId      业务ID
	 * @return 解绑状态
	 */
	public static Boolean unbindBsId(AioConfig aioConfig,
									 String bsId) {
		return unbindBsId(aioConfig, bsId, null);
	}

	/**
	 * 解绑指定用户的业务ID，提供业务ID和用户上下文信息
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param bsId           业务ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindBsId(AioConfig aioConfig,
									 String bsId,
									 ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.Bs_ID)
				.remove(bsId, channelContext);
	}

	/**
	 * 解绑客户端节点
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindClientNode(ChannelContext channelContext) {
		return unbindClientNode(channelContext.getAioConfig(), null, channelContext);
	}

	/**
	 * 解绑客户端节点
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node      客户端 {@link Node}
	 * @return 解绑状态
	 */
	public static Boolean unbindClientNode(AioConfig aioConfig,
										   Node node) {
		return unbindClientNode(aioConfig, node, null);
	}

	/**
	 * 解绑客户端节点
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param node           客户端 {@link Node}
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindClientNode(AioConfig aioConfig,
										   Node node,
										   ChannelContext channelContext) {
		String clientNode = null;
		if (Objects.nonNull(node)) {
			clientNode = node.getAddr();
		}
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.CLIENT_NODE_ID)
				.remove(clientNode, channelContext);
	}

	/**
	 * 从指定集群组内解绑
	 *
	 * @param cluId          集群ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindClu(String cluId,
									ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext.
				getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.remove(cluId, channelContext);
	}

	/**
	 * 从所有集群组内都解绑
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindFromAllClu(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.CLU_ID)
				.removeAll(channelContext);
	}

	/**
	 * 从指定群组内解绑
	 *
	 * @param groupId        群组ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindGroup(String groupId,
									  ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.remove(groupId, channelContext);
	}

	/**
	 * 从所有群组中解绑
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindFromAllGroup(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.GROUP_ID)
				.removeAll(channelContext);
	}

	/**
	 * 解绑ID
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindId(ChannelContext channelContext) {
		return unbindId(channelContext.getAioConfig(), null, channelContext);
	}

	/**
	 * 解绑ID
	 *
	 * @param aioConfig 配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id        ID
	 * @return 解绑状态
	 */
	public static Boolean unbindId(AioConfig aioConfig,
								   String id) {
		return unbindId(aioConfig, id, null);
	}

	/**
	 * 解绑ID
	 *
	 * @param aioConfig      配置信息 {@link cn.starboot.socket.core.AioConfig}
	 * @param id             ID
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindId(AioConfig aioConfig,
								   String id,
								   ChannelContext channelContext) {
		return aioConfig
				.getMaintainManager()
				.getCommand(MaintainEnum.ID)
				.remove(id, channelContext);
	}

	/**
	 * 根据指定IP解绑用户
	 *
	 * @param ip             IP
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindIp(String ip,
								   ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.remove(ip, channelContext);
	}

	/**
	 * 从所有IP组内解绑用户
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindFromAllIp(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.IP)
				.removeAll(channelContext);
	}

	/**
	 * 根据TOKEN解绑用户
	 *
	 * @param token          TOKEN
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindToken(String token,
									  ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.remove(token, channelContext);
	}

	/**
	 * 从所有TOKEN组内解绑用户
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindFromAllToken(ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.TOKEN)
				.removeAll(channelContext);
	}

	/**
	 * 根据USER解绑用户
	 *
	 * @param user           USER
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
	public static Boolean unbindUser(String user,
									 ChannelContext channelContext) {
		if (Objects.isNull(channelContext)) return false;
		return channelContext
				.getAioConfig()
				.getMaintainManager()
				.getCommand(MaintainEnum.USER)
				.remove(user, channelContext);
	}

	/**
	 * 从所有USER组内解绑用户
	 *
	 * @param channelContext 用户上下文信息 {@link cn.starboot.socket.core.ChannelContext}
	 * @return 解绑状态
	 */
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

	// ---------------------------------end----------------------------------

	public static class UtilApi {

		/**
		 * aio-socket 作者自研超大包解决方案
		 *
		 * @param buffer         虚拟buffer
		 * @param needLength     需要读取的长度
		 * @param usedLength     已经读取的长度
		 * @param channelContext 用户通道上下文信息
		 * @return               byte数组
		 */
		public static byte[] getBytesFromByteBuffer(MemoryUnit buffer, int needLength, int usedLength, ChannelContext channelContext) {
			// 小包消息处理
			if (channelContext.getOldByteBuffer().isEmpty()) {
				// 数据够用，直接读
				if (needLength <= buffer.buffer().remaining()) {
					byte[] bytes = new byte[needLength];
					buffer.buffer().get(bytes);
					return bytes;
				}
				// 数据不够
				return null;
			}
			// 大包消息处理，检查队列数据是否足够
			final int readBufferSize = channelContext.getAioConfig().getReadBufferSize();
			if (needLength + usedLength <= channelContext.getOldByteBuffer().size() * readBufferSize) {
				// 队列数据够，则读数据
				byte[] bytes = new byte[needLength];
				int index = 0;
				MemoryUnit oldBuffer;
				while ((oldBuffer = channelContext.getOldByteBuffer().poll()) != null && index <= needLength) {
					int relatable = Math.min(needLength - index, oldBuffer.buffer().remaining());
					oldBuffer.buffer().get(bytes, index, relatable);
					index += relatable;
					if (channelContext.getReadBuffer() != oldBuffer) {
						oldBuffer.clean();
					}
				}
				return bytes;
			}
			// 若队列数据不够则继续读
			return null;
		}

	}
}
