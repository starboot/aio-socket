package io.github.mxd888.websocket.demo;

import io.github.mxd888.websocket.WebSocketHandler;
import io.github.mxd888.websocket.pojo.Session;
import io.netty.handler.codec.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL(mxd) on 2022/8/10 12:38
 */
public class myWs implements WebSocketHandler {
    @Override
    public void beforeHandshake(Session session, HttpHeaders headers, Map<String, List<String>> pathMap, Map<String, String> arg) {

        System.out.println("握手之前");
    }

    @Override
    public void onOpen(Session session, HttpHeaders headers, Map<String, List<String>> pathMap, Map<String, String> arg) {

        System.out.println(session.toString());
        System.out.println(headers.toString());
        System.out.println(pathMap.toString());
        System.out.println(arg.toString());
        System.out.println("开启");
    }

    @Override
    public void onClose(Session session) throws IOException {

        System.out.println("关闭");
    }

    @Override
    public void onError(Session session, Throwable throwable) {

        System.out.println("出错");
    }

    @Override
    public void onMessage(Session session, String message) {

        System.out.println(message);
        session.sendText("服务器收到了：" + message);
    }

    @Override
    public void onBinary(Session session, byte[] bytes) {
        System.out.println("比特");
    }

    @Override
    public void onEvent(Session session, Object evt) {

        System.out.println("状态机");
    }
}
