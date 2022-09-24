package io.github.mxd888.demo.server;


import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.utils.pool.buffer.BufferPagePool;
import io.github.mxd888.socket.core.ServerBootstrap;
import io.github.mxd888.socket.plugins.ACKPlugin;
import io.github.mxd888.socket.plugins.HeartPlugin;
import io.github.mxd888.socket.plugins.MonitorPlugin;

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
 * <p>
 * i7 9200
 * <p>
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
 * <p>
 * <p>
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
 * <p>
 * <p>
 * <p>
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
 * <p>
 * <p>
 * 开启零拷贝后性能
 * -----5seconds ----
 * inflow:		730.9888458251953(MB)
 * outflow:	723.3613929748535(MB)
 * process fail:	0
 * process count:	38165869
 * process total:	116162983
 * read count:	724	write count:	87277
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	7633173.8
 * Transfer/sec:	146.19776916503906(MB)
 * <p>
 * -----5seconds ----
 * inflow:		414.99841690063477(MB)
 * outflow:	413.89660453796387(MB)
 * process fail:	0
 * process count:	72276676
 * process total:	213580975
 * read count:	416	write count:	109193
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	1.44553352E7
 * Transfer/sec:	82.99968338012695(MB)
 * <p>
 * <p>
 * 没有开启零拷贝
 * -----5seconds ----
 * inflow:		665.9898376464844(MB)
 * outflow:	663.3517456054688(MB)
 * process fail:	0
 * process count:	34905530
 * process total:	97386647
 * read count:	667	write count:	7090
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	6981106.0
 * Transfer/sec:	133.19796752929688(MB)
 * <p>
 * <p>
 * -----5seconds ----
 * inflow:		347.99867248535156(MB)
 * outflow:	346.63309478759766(MB)
 * process fail:	0
 * process count:	60999573
 * process total:	221453433
 * read count:	352	write count:	95129
 * connect count:	0
 * disconnect count:	0
 * online count:	9
 * connected total:	10
 * Requests/sec:	1.21999146E7
 * Transfer/sec:	69.59973449707032(MB)
 * <p>
 * Process finished with exit code -1
 */
public class Server {

    // java -jar ***** host port
    public static void main(String[] args) {

        ServerBootstrap bootstrap = new ServerBootstrap((args != null && args.length != 0) ? args[0] : "127.0.0.1", (args != null && args.length != 0) ? Integer.parseInt(args[1]) : 8888, new ServerHandler());
        if (args != null && args.length > 0) {
            System.out.println(args[0] + "--" + Integer.parseInt(args[1]));
        }
        bootstrap.getConfig()
                .setEnhanceCore(true)
                .setBufferFactory(() -> new BufferPagePool(5 * 1024 * 1024 * 2, 9, true))
                .setReadBufferSize(1024 * 1024)
                .setWriteBufferSize(1024 * 1024)
                .setWriteBufferCapacity(16)
                // 使插件功能生效
                .setEnablePlugins(true)
                // 注册服务器统计插件
                .getPlugins()
                // 注册流量监控插件
//                .addPlugin(new StreamMonitorPlugin())
                .addPlugin(new MonitorPlugin(5))
                .addPlugin(new HeartPlugin(30, TimeUnit.SECONDS) {
                    @Override
                    public boolean isHeartMessage(Packet packet) {
                        if (packet instanceof DemoPacket) {
                            DemoPacket packet1 = (DemoPacket) packet;
                            return packet1.getData().equals("heart message");
                        }
                        return false;
                    }
                })
                .addPlugin(new ACKPlugin(30, TimeUnit.SECONDS, (context, lastTime) -> {
                    System.out.println("超时了：..." + lastTime);
                }));
        bootstrap.start();

    }
}
