package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.common.utils.ByteTree;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.server.HttpServerConfiguration;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
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
