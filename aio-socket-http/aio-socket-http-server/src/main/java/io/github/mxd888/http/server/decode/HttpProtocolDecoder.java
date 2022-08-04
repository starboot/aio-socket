/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: RequestLineDecoder.java
 * Date: 2020-03-30
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package io.github.mxd888.http.server.decode;



import io.github.mxd888.http.common.utils.ByteTree;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/30
 */
class HttpProtocolDecoder extends AbstractDecoder {

    private final HttpHeaderDecoder decoder = new HttpHeaderDecoder(getConfiguration());

    private final LfDecoder lfDecoder = new LfDecoder(decoder, getConfiguration());

    public HttpProtocolDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request) {
        ByteTree<?> protocol = StringUtils.scanByteTree(byteBuffer, CR_END_MATCHER, getConfiguration().getByteCache());
        if (protocol != null) {
            request.setProtocol(protocol.getStringValue());
            return lfDecoder.decode(byteBuffer, channelContext, request);
        } else {
            return this;
        }

    }
}
