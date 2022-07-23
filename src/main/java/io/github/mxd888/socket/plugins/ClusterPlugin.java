package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.cluster.ClusterEntity;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 集群插件
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClusterPlugin extends AbstractPlugin {

    @Override
    public boolean beforeProcess(ChannelContext channelContext, Packet packet) {
        AioConfig config = channelContext.getAioConfig();
        if (Objects.nonNull(packet.getEntity())) {
            if (packet.getEntity().isAuth()) {
                // 绑定到集群组
                config.getGroups().join("ClusterServer", channelContext);
                // 绑定集群ID
                config.getIds().join(packet.getFromId(), channelContext);
                // 设置通道ID
                channelContext.setId(packet.getFromId());
            }else {
                // 该消息为绑定消息，将用户与指定集群中的机器进行绑定
                config.getClusterIds().join(packet.getToId(), channelContext.getId());
            }
            return false;
        }
        // 获取接收方所在ServerIP
        String s = config.getClusterIds().get(packet.getToId());
        // 判断接收方是否在本服务器
        if (s.equals(config.getHost())) {
            // 执行处理逻辑
            return true;
        }
        // 否则发送到集群服务器
        Aio.send(config.getIds().get(s), packet);
        // 本服务器不做处理逻辑
        return false;
    }

    @Override
    public void beforeDecode(VirtualBuffer readBuffer, ChannelContext channelContext, Packet packet) {
        ByteBuffer buffer = readBuffer.buffer();
        int anInt = buffer.getInt();
        byte[] b = new byte[anInt];
        readBuffer.buffer().get(b);
        packet.setFromId(new String(b));
        int bnInt = buffer.getInt();
        byte[] b1 = new byte[bnInt];
        readBuffer.buffer().get(b);
        packet.setToId(new String(b1));
        int cnInt = buffer.getInt();
        if (cnInt == 1) {
            packet.setEntity(new ClusterEntity(true));
        } else if (cnInt == 2) {
            packet.setEntity(new ClusterEntity(false));
        }else {
            packet.setEntity(null);
        }
    }

    @Override
    public void beforeEncode(Packet packet, ChannelContext channelContext, VirtualBuffer writeBuffer) {
        ByteBuffer buffer = writeBuffer.buffer();
        buffer.putInt(packet.getFromId().length());
        buffer.put(packet.getFromId().getBytes());
        buffer.putInt(packet.getToId().length());
        buffer.put(packet.getToId().getBytes());
        if (Objects.nonNull(packet.getEntity())) {
            if (packet.getEntity().isAuth()) {
                buffer.putInt(1);
            } else {
                buffer.putInt(2);
            }
        } else {
            buffer.putInt(0);
        }
    }
}
