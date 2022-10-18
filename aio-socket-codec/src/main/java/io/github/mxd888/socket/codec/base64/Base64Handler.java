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
package io.github.mxd888.socket.codec.base64;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.intf.Handler;
import io.github.mxd888.socket.intf.IProtocol;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

public abstract class Base64Handler extends AioHandler implements Handler, IProtocol {

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof Base64Packet) {
            return handle(channelContext, (Base64Packet) packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
        return null;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {

    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.BASE64;
    }

    public abstract Packet handle(ChannelContext channelContext, Base64Packet packet);
}
