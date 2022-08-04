package io.github.mxd888.demo.server.tcp.server;


import io.github.mxd888.demo.server.tcp.DemoPacket;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ServerBootstrap;
import io.github.mxd888.socket.plugins.HeartPlugin;
import io.github.mxd888.socket.plugins.MonitorPlugin;
import io.github.mxd888.socket.plugins.StreamMonitorPlugin;

import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap("127.0.0.1",8888, new ServerHandler());
        bootstrap.getConfig().setEnablePlugins(true);
        bootstrap.getConfig().getPlugins().addPlugin(new HeartPlugin(60, TimeUnit.SECONDS) {
            @Override
            public boolean isHeartMessage(Packet packet) {
                if (packet instanceof DemoPacket) {
                    DemoPacket packet1 = (DemoPacket) packet;
                    return packet1.getData().equals("heart message");
                }
                return false;
            }
        });
        bootstrap.getConfig().getPlugins().addPlugin(new MonitorPlugin());
        bootstrap.getConfig().getPlugins().addPlugin(new StreamMonitorPlugin());
        bootstrap.start();

    }
}
