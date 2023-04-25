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
import cn.starboot.socket.maintain.impl.Groups;
import cn.starboot.socket.utils.lock.SetWithLock;

import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * 基础常用API
 * 绑定ID、绑定群组、单发、群发、移出群、关闭连接
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Aio {

	public static boolean bindBs(String bsId, ChannelContext channelContext){
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.Bs_ID).join(bsId, channelContext);
	}

	public static boolean bindCliNode(String cliNode, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.CLIENT_NODE_ID).join(cliNode, channelContext);
	}

	public static boolean bindClu(String CluId, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.CLU_ID).join(CluId, channelContext);
	}

	public static boolean bindGroup(String groupId, ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.GROUP_ID).join(groupId, channelContext);
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

    /**
     * 异步发送/同步发送 (使用同步发送时，在确保开启ACKPlugin后，只需要将Packet中Req字段赋值即可)
     *
     * @param channelContext 接收方通道
     * @param packet         数据包
     */
    public static void send(ChannelContext channelContext, Packet packet) {
        send0(channelContext, packet, false);
    }

	private static void send0(ChannelContext channelContext, Packet packet, boolean isBlock) {
		channelContext.sendPacket(packet, isBlock);
	}

    public static void bSend(ChannelContext channelContext, Packet packet) {
        send0(channelContext, packet, true);
    }

    public static void sendToId(String channelContextId, Packet packet, AioConfig config) {
        ChannelContext Id = getChannelContextById(channelContextId, config);
        send(Id, packet);
    }

    public static ChannelContext getChannelContextById(String channelContextId, AioConfig config) {
        return config.getMaintainManager().getCommand(MaintainEnum.ID).get(channelContextId, ChannelContext.class);
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
			}else {
				if (o instanceof ChannelContext && !channelContextFilter.filter((ChannelContext) o)) {
					send0((ChannelContext) o, packet, isBlock);
				}
			}
		});
    }

    public static boolean removeUserFromAllGroup(ChannelContext channelContext) {
		return channelContext.getAioConfig().getMaintainManager().getCommand(MaintainEnum.GROUP_ID).removeAll(channelContext.getId(), channelContext);
    }

//    public static boolean removeUserFromGroup(ChannelContext channelContext, String groupId) {
//        return channelContext.getAioConfig().getGroups().remove(groupId, channelContext);
//    }

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
}
