package io.github.mxd888.demo.server.tcp.server;


import io.github.mxd888.socket.core.ServerBootstrap;

public class Server {

    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap("127.0.0.1",8888, new ServerHandler());
        bootstrap.getConfig().setEnablePlugins(true);
//        bootstrap.getConfig().setEnableCluster(true);
//        bootstrap.setCluster(new String[]{"127.0.0.1:9999"});
        bootstrap.start();

    }
}
