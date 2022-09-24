package io.github.mxd888.demo.server;

import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.demo.common.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.TCPChannelContext;

public class ServerHandler extends Handler {

    @Override
    public Packet handle(TCPChannelContext channelContext, Packet packet) {
        if (packet instanceof DemoPacket) {
            return packet;
        }
        return null;
    }
}
