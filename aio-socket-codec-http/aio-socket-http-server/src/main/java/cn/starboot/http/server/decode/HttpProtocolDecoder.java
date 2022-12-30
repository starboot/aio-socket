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

import cn.starboot.http.common.utils.ByteTree;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.HttpServerConfiguration;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpProtocolDecoder extends AbstractDecoder {

    private final HttpHeaderDecoder decoder = new HttpHeaderDecoder(getConfiguration());

    private final LfDecoder lfDecoder = new LfDecoder(decoder, getConfiguration());

    public HttpProtocolDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
        ByteTree<?> protocol = StringUtils.scanByteTree(byteBuffer, CR_END_MATCHER, getConfiguration().getByteCache());
        if (protocol != null) {
            HTTPRequestPacket.setProtocol(protocol.getStringValue());
            return lfDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
        } else {
            return this;
        }

    }
}
