package io.github.mxd888.demo.server;


import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ServerBootstrap;
import io.github.mxd888.socket.plugins.HeartPlugin;
import io.github.mxd888.socket.plugins.MonitorPlugin;
import io.github.mxd888.socket.plugins.StreamMonitorPlugin;

import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap("127.0.0.1",8888, new ServerHandler());
        // 使插件功能生效
        bootstrap.getConfig().setEnablePlugins(true);
        // 注册流量监控插件
//        bootstrap.getConfig().getPlugins().addPlugin(new StreamMonitorPlugin());
        // 注册服务器统计插件
        bootstrap.getConfig().getPlugins().addPlugin(new MonitorPlugin());
        // 注册心跳插件
        bootstrap.getConfig().getPlugins().addPlugin(new HeartPlugin(30, TimeUnit.SECONDS) {
            @Override
            public boolean isHeartMessage(Packet packet) {
                if (packet instanceof DemoPacket) {
                    DemoPacket packet1 = (DemoPacket) packet;
                    return packet1.getData().equals("heart message");
                }
                return false;
            }
        });
        bootstrap.start();

    }
}
