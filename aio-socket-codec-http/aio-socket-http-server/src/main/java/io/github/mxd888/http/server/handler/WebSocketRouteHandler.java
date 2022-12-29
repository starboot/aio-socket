package io.github.mxd888.http.server.handler;

import io.github.mxd888.http.common.logging.Logger;
import io.github.mxd888.http.common.logging.LoggerFactory;
import io.github.mxd888.http.common.utils.AntPathMatcher;
import io.github.mxd888.http.server.WebSocketHandler;
import io.github.mxd888.http.server.WebSocketRequest;
import io.github.mxd888.http.server.WebSocketResponse;
import io.github.mxd888.http.server.impl.HttpRequestPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class WebSocketRouteHandler extends WebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketRouteHandler.class);
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    /**
     * 默认404
     */
    private final WebSocketHandler defaultHandler = new WebSocketHandler() {
        @Override
        public void handle(WebSocketRequest request, WebSocketResponse response) throws IOException {
            LOGGER.warn("not found");
        }
    };
    private final Map<String, WebSocketHandler> handlerMap = new ConcurrentHashMap<>();

    @Override
    public void onClose(HttpRequestPacket HTTPRequestPacket) {
        handlerMap.get(HTTPRequestPacket.getRequestURI()).onClose(HTTPRequestPacket);
    }

    @Override
    public void onHeaderComplete(HttpRequestPacket HTTPRequestPacket) throws IOException {
        String uri = HTTPRequestPacket.getRequestURI();
        WebSocketHandler httpHandler = handlerMap.get(uri);
        if (httpHandler == null) {
            for (Map.Entry<String, WebSocketHandler> entity : handlerMap.entrySet()) {
                if (PATH_MATCHER.match(entity.getKey(), uri)) {
                    httpHandler = entity.getValue();
                    break;
                }
            }
            if (httpHandler == null) {
                httpHandler = defaultHandler;
            }
            handlerMap.put(uri, httpHandler);
        }
        httpHandler.onHeaderComplete(HTTPRequestPacket);
    }

    @Override
    public boolean onBodyStream(ByteBuffer byteBuffer, HttpRequestPacket HTTPRequestPacket) {
        return handlerMap.get(HTTPRequestPacket.getRequestURI()).onBodyStream(byteBuffer, HTTPRequestPacket);
    }

    @Override
    public void handle(WebSocketRequest request, WebSocketResponse response) throws IOException {
        String uri = request.getRequestURI();
        WebSocketHandler httpHandler = handlerMap.get(uri);
        httpHandler.handle(request, response);
    }

    /**
     * 配置URL路由
     *
     * @param urlPattern url匹配
     * @param httpHandle 处理handle
     * @return
     */
    public WebSocketRouteHandler route(String urlPattern, WebSocketHandler httpHandle) {
        handlerMap.put(urlPattern, httpHandle);
        return this;
    }
}
