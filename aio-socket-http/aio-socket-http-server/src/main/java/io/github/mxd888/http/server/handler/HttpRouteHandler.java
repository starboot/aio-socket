package io.github.mxd888.http.server.handler;

import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.utils.AntPathMatcher;
import io.github.mxd888.http.server.HttpRequest;
import io.github.mxd888.http.server.HttpResponse;
import io.github.mxd888.http.server.HttpServerHandler;
import io.github.mxd888.http.server.ServerHandler;
import io.github.mxd888.http.server.impl.Request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class HttpRouteHandler extends HttpServerHandler {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    /**
     * 默认404
     */
    private final HttpServerHandler defaultHandler = new HttpServerHandler() {
        @Override
        public void handle(HttpRequest request, HttpResponse response) throws IOException {
            response.setHttpStatus(HttpStatus.NOT_FOUND);
        }
    };
    private final Map<String, HttpServerHandler> handlerMap = new ConcurrentHashMap<>();

    @Override
    public void onHeaderComplete(Request request) throws IOException {
        ServerHandler httpServerHandler = matchHandler(request);
        //注册 URI 与 Handler 的映射关系
        request.getConfiguration().getUriByteTree().addNode(request.getUri(), httpServerHandler);
        //更新本次请求的实际 Handler
        request.setServerHandler(httpServerHandler);
        httpServerHandler.onHeaderComplete(request);
    }

    @Override
    public boolean onBodyStream(ByteBuffer buffer, Request request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onClose(Request request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, CompletableFuture<Object> completableFuture) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * 配置URL路由
     *
     * @param urlPattern  url匹配
     * @param httpHandler 处理handler
     * @return
     */
    public HttpRouteHandler route(String urlPattern, HttpServerHandler httpHandler) {
        handlerMap.put(urlPattern, httpHandler);
        return this;
    }

    private HttpServerHandler matchHandler(Request request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return defaultHandler;
        }
        HttpServerHandler httpHandler = handlerMap.get(uri);
        if (httpHandler == null) {
            for (Map.Entry<String, HttpServerHandler> entity : handlerMap.entrySet()) {
                if (PATH_MATCHER.match(entity.getKey(), uri)) {
                    httpHandler = entity.getValue();
                    break;
                }
            }
            if (httpHandler == null) {
                httpHandler = defaultHandler;
            }
        }
        return httpHandler;
    }
}
