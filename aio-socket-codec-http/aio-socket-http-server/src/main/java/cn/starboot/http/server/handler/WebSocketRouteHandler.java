/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.http.server.handler;

import cn.starboot.http.common.logging.Logger;
import cn.starboot.http.common.logging.LoggerFactory;
import cn.starboot.http.common.utils.AntPathMatcher;
import cn.starboot.http.server.WebSocketHandler;
import cn.starboot.http.server.WebSocketRequest;
import cn.starboot.http.server.WebSocketResponse;
import cn.starboot.http.server.impl.HttpRequestPacket;

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
