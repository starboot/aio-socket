package io.github.mxd888.socket.core;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.cluster.ClusterEntity;
import io.github.mxd888.socket.maintain.Groups;

/**
 *
 * 基础常用API
 * 绑定ID、绑定群组、单发、群发、移出群、关闭连接
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Aio {

    /**
     * 绑定ID跟通道的联系
     *
     * @param id             用户ID
     * @param channelContext 用户通道
     */
    public static void bindID(String id, ChannelContext channelContext) {
        // 绑定ID
        AioConfig config = channelContext.getAioConfig();
        config.getIds().join(id, channelContext);
        channelContext.setId(id);
        if (config.isEnableCluster()) {
            // 绑定用户ID与登录机器的关系
            config.getClusterIds().join(id, config.getHost());
            // 通知其他集群机器
            Packet packet = new Packet();
            packet.setToId(id);
            packet.setEntity(new ClusterEntity(false));
            VirtualBuffer encode = config.getHandler().encode(packet, channelContext, channelContext.getByteBuf());
            config.getGroups().writeToGroup("ClusterServer", encode, null);
        }
    }

    /**
     * 绑定群组
     *
     * @param groupId        群组ID
     * @param channelContext 用户通道
     */
    public static void bindGroup(String groupId, ChannelContext channelContext) {
        // 绑定群组
        Groups groups = channelContext.getAioConfig().getGroups();
        groups.join(groupId, channelContext);
    }

    /**
     * 异步发送
     *
     * @param channelContext 接收方通道
     * @param packet         数据包
     */
    public static void send(ChannelContext channelContext, Packet packet) {
        VirtualBuffer buffer = channelContext.getAioConfig().getHandler().encode(packet, channelContext, channelContext.getByteBuf());
        send(channelContext, buffer);
    }

    /**
     * 执行发送
     *
     * @param channelContext 接收方通道
     * @param buffer         待发送比特流
     */
    public static void send(ChannelContext channelContext, VirtualBuffer buffer) {
        channelContext.writeBuffer().write(buffer);
        channelContext.writeBuffer().flush();
    }

    /**
     * 群发
     *
     * @param groupId        群组ID
     * @param packet         消息包
     * @param channelContext 发送者上下文
     */
    public static void sendGroup(String groupId, Packet packet, ChannelContext channelContext) {
        VirtualBuffer buffer = channelContext.getAioConfig().getHandler().encode(packet, channelContext, channelContext.getByteBuf());
        channelContext.getAioConfig().getGroups().writeToGroup(groupId, buffer, channelContext);
    }

    public static void removeUserFromAllGroup(ChannelContext channelContext) {
        channelContext.getAioConfig().getGroups().remove(channelContext);
    }

    public static void removeUserFromGroup(ChannelContext channelContext, String groupId) {
        channelContext.getAioConfig().getGroups().remove(groupId, channelContext);
    }

    /**
     * 关闭某个连接
     *
     * @param channelContext 通道上下文
     */
    public static void close(ChannelContext channelContext) {
        removeUserFromAllGroup(channelContext);
        channelContext.getAioConfig().getIds().remove(channelContext.getId());
        channelContext.close();
    }
}
