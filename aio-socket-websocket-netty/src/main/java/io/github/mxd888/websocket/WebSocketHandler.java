package io.github.mxd888.websocket;


import io.github.mxd888.websocket.pojo.Session;
import io.netty.handler.codec.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL(mxd) on 2022/8/9 18:45
 */
public interface WebSocketHandler {

    void beforeHandshake(Session session, HttpHeaders headers, Map<String, List<String>> requestParamMap, Map<String, String> arg);

    void onOpen(Session session, HttpHeaders headers, Map<String, List<String>> requestParamMap, Map<String, String> arg);

    void onClose(Session session) throws IOException;

    void onError(Session session, Throwable throwable);

    void onMessage(Session session, String message);

    void onBinary(Session session, byte[] bytes);

    void onEvent(Session session, Object evt);

}
