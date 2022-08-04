package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.common.utils.ByteTree;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.server.HttpServerConfiguration;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2021/6/10
 */
public abstract class AbstractDecoder implements Decoder {
    protected static final ByteTree.EndMatcher CR_END_MATCHER = endByte -> endByte == Constant.CR;
    protected static final ByteTree.EndMatcher SP_END_MATCHER = endByte -> endByte == Constant.SP;
    private final HttpServerConfiguration configuration;

    public AbstractDecoder(HttpServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public HttpServerConfiguration getConfiguration() {
        return configuration;
    }
}
