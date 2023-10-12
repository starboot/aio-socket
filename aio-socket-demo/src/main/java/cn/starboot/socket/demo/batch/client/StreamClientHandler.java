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
package cn.starboot.socket.demo.batch.client;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.bytes.BytesHandler;
import cn.starboot.socket.codec.bytes.BytesPacket;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;
import cn.starboot.socket.exception.AioEncoderException;

public class StreamClientHandler extends BytesHandler {

	private static final byte[] data = "hello-aio-socket".getBytes();

	private static final int len = data.length;

	@Override
	public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		WriteBuffer writeBuffer = channelContext.getWriteBuffer();
		int num = (int) (Math.random() * 10) + 1;
		writeBuffer.writeInt(len * num);
		while (num-- > 0) {
			writeBuffer.write(data);
		}
	}

	@Override
	public Packet handle(ChannelContext channelContext, BytesPacket packet) {
		return null;
	}
}