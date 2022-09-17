package io.github.mxd888.demo.server;

import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.demo.common.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;

public class ServerHandler extends Handler {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof DemoPacket) {
            return packet;
        }
        return null;
    }
}
