//package cn.starboot.socket.test.udp.server;
//
//import cn.starboot.socket.udp.UDPBootstrap;
//import cn.starboot.socket.utils.pool.memory.MemoryPool;
//
//import java.io.IOException;
//
//public class UDPServer {
//
//    public static void main(String[] args) throws IOException {
//
//        UDPBootstrap bootstrap = new UDPBootstrap(new ServerUDPHandler());
//        bootstrap
////                .addPlugin(new MonitorPlugin(5))
//                .setBufferFactory(() -> new MemoryPool(1024 * 1024 * 16, Runtime.getRuntime().availableProcessors(), true))
//                .setReadBufferSize(1024).open(8888);
//    }
//}
