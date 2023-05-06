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
package cn.starboot.socket.codec.base64;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.exception.AioEncoderException;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.internal.ObjUtils;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.Packet;
import cn.starboot.socket.enums.ProtocolEnum;
import cn.starboot.socket.exception.AioDecoderException;

import java.nio.ByteBuffer;

/**
 * Base64协议解编码处理器
 *
 * @author MDong
 */
public abstract class Base64Handler implements AioHandler {

	private final boolean breakLines;

	private final Base64Dialect dialect;

	private Base64Handler() {
		this(true);
	}

	private Base64Handler(boolean breakLines) {
		this(breakLines, Base64Dialect.STANDARD);
	}

	private Base64Handler(boolean breakLines, Base64Dialect dialect) {
		this.breakLines = breakLines;
		this.dialect = ObjUtils.checkNotNull(dialect, "dialect");
		System.out.println("Base64 Codec functionality is not yet fully developed");
	}

	@Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof Base64Packet) {
            return handle(channelContext, (Base64Packet) packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit readBuffer, ChannelContext channelContext) throws AioDecoderException {
		ByteBuffer buffer = readBuffer.buffer();
        return Base64.decode(buffer, buffer.position(), buffer.remaining(), dialect);
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		if (packet instanceof Base64Packet) {
			Base64.encode((Base64Packet) packet, 0, 0, channelContext, breakLines, dialect);
		}
    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.BASE64;
    }

    public abstract Packet handle(ChannelContext channelContext, Base64Packet packet);
}
