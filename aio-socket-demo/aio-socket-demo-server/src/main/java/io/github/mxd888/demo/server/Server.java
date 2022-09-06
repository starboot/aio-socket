package io.github.mxd888.demo.server;


import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ServerBootstrap;
import io.github.mxd888.socket.plugins.HeartPlugin;
import io.github.mxd888.socket.plugins.MonitorPlugin;
import io.github.mxd888.socket.plugins.StreamMonitorPlugin;

import java.util.concurrent.TimeUnit;

/**
 * enhance启动前
 * -----60seconds ----
 * inflow:		1683.9334859848022(MB)
 * outflow:	1683.897361755371(MB)
 * process fail:	0
 * process count:	88286662
 * process total:	160878640
 * read count:	865563	write count:	991461
 * connect count:	0
 * disconnect count:	0
 * online count:	11
 * connected total:	11
 * Requests/sec:	1471444.3666666667
 * Transfer/sec:	28.065558099746703(MB)
 * -----60seconds END ----
 * enhance启动后
 * -----60seconds ----
 * inflow:		1710.0232257843018(MB)
 * outflow:	1710.059471130371(MB)
 * process fail:	0
 * process count:	89654663
 * process total:	140941121
 * read count:	878968	write count:	1007756
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	1494244.3833333333
 * Transfer/sec:	28.500387096405028(MB)
 * -----60seconds ----
 * inflow:		1755.8204431533813(MB)
 * outflow:	1755.7857513427734(MB)
 * process fail:	0
 * process count:	92055461
 * process total:	232996582
 * read count:	902505	write count:	1030772
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	1534257.6833333333
 * Transfer/sec:	29.263674052556357(MB)
 */
public class Server {

    // java -jar ***** host port enhanceCore:0=false 1=true
    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap((args != null && args.length != 0) ? args[0] : "127.0.0.1", (args != null && args.length != 0) ? Integer.parseInt(args[1]) : 8888, new ServerHandler());
        if (((args != null && args.length != 0) ? Integer.parseInt(args[2]) : 0) == 1) {
            // 启用内核增强
            bootstrap.getConfig().setEnhanceCore(true);
            System.out.println("启动内核增强");
        }
        if (args != null && args.length > 0) {
            System.out.println(args[0] + "--" + Integer.parseInt(args[1]) + "--" + Integer.parseInt(args[2]));
        }
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
