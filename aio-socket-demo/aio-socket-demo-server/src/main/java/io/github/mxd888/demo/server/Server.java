package io.github.mxd888.demo.server;


import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.buffer.BufferFactory;
import io.github.mxd888.socket.buffer.BufferPagePool;
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
 *
 * i7 9200
 * -----60seconds ----
 * inflow:		4072.0993843078613(MB)
 * outflow:	4072.073097229004(MB)
 * process fail:	0
 * process count:	213495282
 * process total:	213495282
 * read count:	2093101	write count:	2765627
 * connect count:	10
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	3558254.7
 * Transfer/sec:	67.86832307179769(MB)
 *
 * -----60seconds ----
 * inflow:		4569.511960029602(MB)
 * outflow:	4569.517879486084(MB)
 * process fail:	0
 * process count:	239573978
 * process total:	453069260
 * read count:	2348762	write count:	3234584
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	3992899.6333333333
 * Transfer/sec:	76.15853266716003(MB)
 *
 * -----60seconds ----
 * inflow:		4421.979174613953(MB)
 * outflow:	4421.972923278809(MB)
 * process fail:	0
 * process count:	231838817
 * process total:	684908077
 * read count:	2272930	write count:	3166874
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	3863980.283333333
 * Transfer/sec:	73.69965291023254(MB)
 *
 * -----60seconds ----
 * inflow:		4408.335428237915(MB)
 * outflow:	4408.3258056640625(MB)
 * process fail:	0
 * process count:	231123967
 * process total:	916032044
 * read count:	2265922	write count:	3135271
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	3852066.1166666667
 * Transfer/sec:	73.47225713729858(MB)
 *
 * 开启零拷贝后只有900多万/min 开启之前24000万/min
 * -----60seconds ----
 * inflow:		171.94686889648438(MB)
 * outflow:	171.93531036376953(MB)
 * process fail:	0
 * process count:	9014552
 * process total:	9014552
 * read count:	88382	write count:	9014372
 * connect count:	10
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	150242.53333333333
 * Transfer/sec:	2.8657811482747397(MB)
 *
 *
 *
 * -----60seconds ----
 * inflow:		5581.945913314819(MB)
 * outflow:	5581.941890716553(MB)
 * process fail:	0
 * process count:	292654665
 * process total:	572137029
 * read count:	2869162	write count:	3576363
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	4877577.75
 * Transfer/sec:	93.03243188858032(MB)
 *
 * Process finished with exit code -1
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
        bootstrap.getConfig().setEnhanceCore(true);
        if (args != null && args.length > 0) {
            System.out.println(args[0] + "--" + Integer.parseInt(args[1]) + "--" + Integer.parseInt(args[2]));
        }
        bootstrap.getConfig().setBufferFactory(() -> new BufferPagePool(5 * 1024 * 1024 * 2, 9, false));
        bootstrap.getConfig().setReadBufferSize(1024 * 1024);
        bootstrap.getConfig().setWriteBufferSize(1024 * 1024);
        bootstrap.getConfig().setWriteBufferCapacity(16);
        // 使插件功能生效
        bootstrap.getConfig().setEnablePlugins(true);
        // 注册流量监控插件
//        bootstrap.getConfig().getPlugins().addPlugin(new StreamMonitorPlugin());
        // 注册服务器统计插件
        bootstrap.getConfig().getPlugins().addPlugin(new MonitorPlugin(5));
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
