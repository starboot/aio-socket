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
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.server.HttpServerConfiguration;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class LfDecoder extends AbstractDecoder {
    private final AbstractDecoder nextDecoder;

    public LfDecoder(AbstractDecoder nextDecoder, HttpServerConfiguration configuration) {
        super(configuration);
        this.nextDecoder = nextDecoder;
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket HTTPRequestPacket) {
        if (byteBuffer.hasRemaining()) {
            if (byteBuffer.get() != Constant.LF) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            return nextDecoder.decode(byteBuffer, channelContext, HTTPRequestPacket);
        }
        return this;
    }
}
