package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.cluster.ClusterEntity;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Created by DELL(mxd) on 2022/7/23 20:50
 */
public class KernelProtocolPlugin extends AbstractPlugin{

    @Override
    public void beforeDecode(VirtualBuffer readBuffer, ChannelContext channelContext, Packet packet) {
        ByteBuffer buffer = readBuffer.buffer();
        int anInt = buffer.getInt();
        byte[] b = new byte[anInt];
        readBuffer.buffer().get(b);
        packet.setFromId(new String(b));
        int bnInt = buffer.getInt();
        byte[] b1 = new byte[bnInt];
        readBuffer.buffer().get(b1);
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
