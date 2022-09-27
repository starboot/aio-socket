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
 * i7 9200  3.0GHZ
 * <p>
 * 开启零拷贝后性能
 * -----5seconds ----
 * inflow:		747.0399322509766(MB)
 * outflow:	746.5587615966797(MB)
 * process fail:	0
 * process count:	39199499
 * process total:	53816106
 * read count:	748	write count:	154068
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	7839899.8
 * Transfer/sec:	149.40798645019532(MB)
 * </p>
 *
 * <p>
 * 没有开启零拷贝
 * -----5seconds ----           (这次的非零拷贝测试结果很优秀不知道为什么)
 * inflow:		763.9883422851562(MB)
 * outflow:	763.6293029785156(MB)
 * process fail:	0
 * process count:	40062578
 * process total:	85445242
 * read count:	763	write count:	65529
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	8012515.6
 * Transfer/sec:	152.79766845703125(MB)
 * </p>
 * <p>
 * -----5seconds ----           (这次的属于正常，运行两分钟的均值)
 * inflow:		542.9917144775391(MB)
 * outflow:	544.2631149291992(MB)
 * process fail:	0
 * process count:	28531604
 * process total:	304770531
 * read count:	543	write count:	308183
 * connect count:	0
 * disconnect count:	0
 * online count:	2
 * connected total:	10
 * Requests/sec:	5706320.8
 * Transfer/sec:	108.59834289550781(MB)
 * </p>
 * 可以看到零拷贝其实在数据量不大的情况下，性能跟非零拷贝并不优秀还可能略差
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
                .setBufferFactory(() -> new BufferPagePool(50 * 1024 * 1024 * 2, 10, false))
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
