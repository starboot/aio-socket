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
package cn.starboot.http.server.decode;

import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.exception.HttpException;
import cn.starboot.http.common.utils.ByteTree;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.HttpServerConfiguration;
import cn.starboot.http.server.ServerHandler;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpUriDecoder extends AbstractDecoder {
    private static final ByteTree.EndMatcher URI_END_MATCHER = endByte -> endByte <= '?' && (endByte == ' ' || endByte == '?');
    private final HttpUriQueryDecoder uriQueryDecoder = new HttpUriQueryDecoder(getConfiguration());
    private final HttpProtocolDecoder protocolDecoder = new HttpProtocolDecoder(getConfiguration());

    public HttpUriDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
        ByteTree<ServerHandler<?, ?>> uriTreeNode = StringUtils.scanByteTree(byteBuffer, URI_END_MATCHER, getConfiguration().getUriByteTree());
        if (uriTreeNode != null) {
            HTTPRequestPacket.setUri(uriTreeNode.getStringValue());
            if (uriTreeNode.getAttach() == null) {
                HTTPRequestPacket.setServerHandler(HTTPRequestPacket.getConfiguration().getHttpServerHandler());
            } else {
                HTTPRequestPacket.setServerHandler(uriTreeNode.getAttach());
            }

            switch (byteBuffer.get(byteBuffer.position() - 1)) {
                case Constant.SP:
                    return protocolDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
                case '?':
                    return uriQueryDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
                default:
                    throw new HttpException(HttpStatus.BAD_REQUEST);
            }
        } else {
            return this;
        }
    }
}
