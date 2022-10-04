package io.github.mxd888.websocket.support;

import io.github.mxd888.websocket.utils.AntPathMatcher;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.mxd888.websocket.pojo.PojoEndpointServer.URI_TEMPLATE;

public class AntPathMatcherWrapper extends AntPathMatcher implements WsPathMatcher {

    private final String pattern;

    public AntPathMatcherWrapper(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        Map<String, String> variables = new LinkedHashMap<>();
        boolean result = doMatch(pattern, decoder.path(), true, variables);
        if (result) {
            channel.attr(URI_TEMPLATE).set(variables);
            return true;
        }
        return false;
    }
}
