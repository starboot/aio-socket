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
import cn.starboot.http.server.impl.HttpRequestHandler;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpHeaderDecoder extends AbstractDecoder {
    private static final ByteTree.EndMatcher COLON_END_MATCHER = endByte -> endByte == Constant.COLON;

    private final HeaderValueDecoder headerValueDecoder = new HeaderValueDecoder();
    private final IgnoreHeaderDecoder ignoreHeaderDecoder = new IgnoreHeaderDecoder();
    private final LfDecoder lfDecoder = new LfDecoder(this, getConfiguration());

    public HttpHeaderDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
        if (HTTPRequestPacket.getHeaderSize() >= 0 && HTTPRequestPacket.getHeaderSize() >= getConfiguration().getHeaderLimiter()) {
            return ignoreHeaderDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
        }
        if (byteBuffer.remaining() < 2) {
            return this;
        }
        //header解码结束
        if (byteBuffer.get(byteBuffer.position()) == Constant.CR) {
            if (byteBuffer.get(byteBuffer.position() + 1) != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            byteBuffer.position(byteBuffer.position() + 2);
//            return decoder.decode(byteBuffer, aioSession, request);
            return HttpRequestHandler.BODY_READY_DECODER;
        }
        //Header name解码
        ByteTree<Function<String, ServerHandler<?, ?>>> name = StringUtils.scanByteTree(byteBuffer, COLON_END_MATCHER, getConfiguration().getHeaderNameByteTree());
        if (name == null) {
            return this;
        }
//        System.out.println("headerName: " + name.getAttach());
        HTTPRequestPacket.setHeaderTemp(name);
        return headerValueDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
    }

    /**
     * Value值解码
     */
    class HeaderValueDecoder implements Decoder {
        @Override
        public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
            ByteTree<?> value = StringUtils.scanByteTree(byteBuffer, CR_END_MATCHER, getConfiguration().getByteCache());
            if (value == null) {
                if (byteBuffer.remaining() == byteBuffer.capacity()) {
                    throw new HttpException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE);
                }
                return this;
            }
//            System.out.println("value: " + value.getStringValue());
            HTTPRequestPacket.setHeadValue(value.getStringValue());
            return lfDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
        }
    }
}
