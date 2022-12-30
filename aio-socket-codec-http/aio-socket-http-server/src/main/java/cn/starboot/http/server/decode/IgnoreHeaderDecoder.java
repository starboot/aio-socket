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

import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.server.impl.HttpRequestHandler;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.socket.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class IgnoreHeaderDecoder implements Decoder {

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, HttpRequestPacket httpHeader) {
        int position = byteBuffer.position() + byteBuffer.arrayOffset();
        int limit = byteBuffer.limit() + byteBuffer.arrayOffset();
        byte[] data = byteBuffer.array();

        while (limit - position >= 4) {
            byte b = data[position + 3];
            if (b == Constant.CR) {
                position++;
                continue;
            } else if (b != Constant.LF) {
                position += 7;
                if (position >= limit || (data[position] == Constant.CR || data[position] == Constant.LF)) {
                    position -= 3;
                }
                continue;
            }
            // header 结束符匹配，最后2字节已经是CR、LF,无需重复验证
            if (data[position] == Constant.CR && data[position + 1] == Constant.LF) {
                byteBuffer.position(position + 4 - byteBuffer.arrayOffset());
                return HttpRequestHandler.BODY_READY_DECODER;
            } else {
                position += 2;
            }
        }
        byteBuffer.position(position - byteBuffer.arrayOffset());
        return this;
    }
}
