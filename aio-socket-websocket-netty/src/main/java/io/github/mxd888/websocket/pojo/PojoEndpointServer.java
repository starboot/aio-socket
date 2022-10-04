package io.github.mxd888.websocket.pojo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.mxd888.websocket.WebSocketHandler;
import io.github.mxd888.websocket.standard.ServerEndpointConfig;
import io.github.mxd888.websocket.support.AntPathMatcherWrapper;
import io.github.mxd888.websocket.support.WsPathMatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author Yeauty
 * @version 1.0
 */
public class PojoEndpointServer {

    private static final AttributeKey<Object> POJO_KEY = AttributeKey.valueOf("WEBSOCKET_IMPLEMENT");

    public static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("WEBSOCKET_SESSION");

    private static final AttributeKey<String> PATH_KEY = AttributeKey.valueOf("WEBSOCKET_PATH");

    public static final AttributeKey<Map<String, String>> URI_TEMPLATE = AttributeKey.valueOf("WEBSOCKET_URI_TEMPLATE");

    public static final AttributeKey<Map<String, List<String>>> REQUEST_PARAM = AttributeKey.valueOf("WEBSOCKET_REQUEST_PARAM");

    private final Map<String, WebSocketHandler> pathMethodMappingMap = new HashMap<>();

    private final ServerEndpointConfig config;

    private final Set<WsPathMatcher> pathMatchers = new HashSet<>();

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PojoEndpointServer.class);

    public PojoEndpointServer(WebSocketHandler methodMapping, ServerEndpointConfig config, String path) {
        addPathPojoMethodMapping(path, methodMapping);
        this.config = config;
    }

    public boolean hasBeforeHandshake(Channel channel, String path) {
        WebSocketHandler webSocketHandler = getPojoMethodMapping(path, channel);
        return ObjectUtil.isNotNull(webSocketHandler);
    }

    public void doBeforeHandshake(Channel channel, FullHttpRequest req, String path) {
        WebSocketHandler webSocketHandler = getPojoMethodMapping(path, channel);
        channel.attr(POJO_KEY).set(webSocketHandler);
        Session session = new Session(channel);
        channel.attr(SESSION_KEY).set(session);
        Map<String, String> pathVariousMap = getPathVariousMap(channel);
        Map<String, List<String>> reqMap = getReqMap(channel, req);
        webSocketHandler.beforeHandshake(session, req.headers(), reqMap, pathVariousMap);
    }

    public void doOnOpen(Channel channel, FullHttpRequest req, String path) {
        WebSocketHandler webSocketHandler = getPojoMethodMapping(path, channel);

        Object implement = channel.attr(POJO_KEY).get();
        if (implement==null){
            try {
                implement = webSocketHandler;
                channel.attr(POJO_KEY).set(implement);
            } catch (Exception e) {
                logger.error(e);
                return;
            }
        }
        Session session = new Session(channel);
        channel.attr(SESSION_KEY).set(session);
        Map<String, String> pathVariousMap = getPathVariousMap(channel);
        Map<String, List<String>> reqMap = getReqMap(channel, req);
        webSocketHandler.onOpen(session, req.headers(), reqMap, pathVariousMap);

    }
    public Map<String, String> getPathVariousMap(Channel channel) {
        Map<String, String> uriTemplateVars = channel.attr(URI_TEMPLATE).get();
        if (!CollUtil.isEmpty(uriTemplateVars)) {
            return uriTemplateVars;
        } else {
            return Collections.emptyMap();
        }
    }

    public Map<String, List<String>> getReqMap(Channel channel, FullHttpRequest request) {
        if (!channel.hasAttr(REQUEST_PARAM)) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            channel.attr(REQUEST_PARAM).set(decoder.parameters());
        }
        return channel.attr(REQUEST_PARAM).get();
    }

    public void doOnClose(Channel channel) throws IOException {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        WebSocketHandler webSocketHandler;
        if (pathMethodMappingMap.size() == 1) {
            webSocketHandler = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            webSocketHandler = pathMethodMappingMap.get(path);
            if (webSocketHandler == null) {
                return;
            }
        }
        webSocketHandler.onClose(new Session(channel));

    }


    public void doOnError(Channel channel, Throwable throwable) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        WebSocketHandler webSocketHandler;
        if (pathMethodMappingMap.size() == 1) {
            webSocketHandler = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            webSocketHandler = pathMethodMappingMap.get(path);
        }
        webSocketHandler.onError(new Session(channel), throwable);
    }

    public void doOnMessage(Channel channel, WebSocketFrame frame) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        WebSocketHandler webSocketHandler;
        if (pathMethodMappingMap.size() == 1) {
            webSocketHandler = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            webSocketHandler = pathMethodMappingMap.get(path);
        }
        TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
        webSocketHandler.onMessage(new Session(channel), textFrame.text());
    }

    public void doOnBinary(Channel channel, WebSocketFrame frame) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        WebSocketHandler webSocketHandler;
        if (pathMethodMappingMap.size() == 1) {
            webSocketHandler = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            webSocketHandler = pathMethodMappingMap.get(path);
        }
        BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
        ByteBuf content = binaryWebSocketFrame.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        webSocketHandler.onBinary(new Session(channel), bytes);

    }

    public void doOnEvent(Channel channel, Object evt) {
        Attribute<String> attrPath = channel.attr(PATH_KEY);
        WebSocketHandler webSocketHandler;
        if (pathMethodMappingMap.size() == 1) {
            webSocketHandler = pathMethodMappingMap.values().iterator().next();
        } else {
            String path = attrPath.get();
            webSocketHandler = pathMethodMappingMap.get(path);
        }
        webSocketHandler.onEvent(new Session(channel), evt);
    }

    public String getHost() {
        return config.getHost();
    }

    public int getPort() {
        return config.getPort();
    }

    public Set<WsPathMatcher> getPathMatcherSet() {
        return pathMatchers;
    }

    public void addPathPojoMethodMapping(String path, WebSocketHandler webSocketHandler) {
        pathMethodMappingMap.put(path, webSocketHandler);
        pathMatchers.add(new AntPathMatcherWrapper(path));
    }

    private WebSocketHandler getPojoMethodMapping(String path, Channel channel) {
        WebSocketHandler methodMapping;
        if (pathMethodMappingMap.size() == 1) {
            methodMapping = pathMethodMappingMap.values().iterator().next();
        } else {
            Attribute<String> attrPath = channel.attr(PATH_KEY);
            attrPath.set(path);
            methodMapping = pathMethodMappingMap.get(path);
            if (methodMapping == null) {
                throw new RuntimeException("path " + path + " is not in pathMethodMappingMap ");
            }
        }
        return methodMapping;
    }
}
