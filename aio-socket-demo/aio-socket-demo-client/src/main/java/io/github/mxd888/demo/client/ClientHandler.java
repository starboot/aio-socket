package io.github.mxd888.demo.client;

import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.demo.common.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.TCPChannelContext;


public class ClientHandler extends Handler {

    private long count = 0L;

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        DemoPacket packet1 = (DemoPacket) packet;
        if (!packet1.getData().equals("hello aio-socket")) {
            System.out.println("不一致，出错啦:" + packet1.getData());
        }else {
            count++;
            System.out.println(count + packet1.getData());
            if (count % 1000000 ==0) {
                System.out.println("已收到" + (count / 1000000) + "百万条消息");
            }
        }
//        count++;
//        if (count % 1000000 ==0) {
//            System.out.println("已收到" + (count / 1000000) + "百万条消息");
//        }
        return null;
    }
}
