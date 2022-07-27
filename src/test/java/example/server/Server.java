package example.server;


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
//        bootstrap.getConfig().getPlugins().addPlugin(new MonitorPlugin());
        // 注册心跳插件
        bootstrap.getConfig().getPlugins().addPlugin(new HeartPlugin(30, TimeUnit.SECONDS));
        // 开启集群插件
//        bootstrap.getConfig().setEnableCluster(true);
        // 若要连接其他服务器集群则打开以下注释
//        bootstrap.setCluster(new String[]{"127.0.0.1:9999"});
        bootstrap.start();

    }
}
