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
package cn.starboot.socket.core.utils;

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;

/**
 * aio-socket-kernel工具包
 * 包含了一些内核通用工具
 *      1、大包解决算法
 *      2、通道关闭
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class AIOUtil {

	/**
	 * aio-socket 作者自研超大包解决方案
	 *
	 * @param buffer         虚拟buffer
	 * @param needLength     需要读取的长度
	 * @param usedLength     已经读取的长度
	 * @param channelContext 用户通道上下文信息
	 * @return               byte数组
	 */
    public static byte[] getBytesFromByteBuffer(MemoryUnit buffer, int needLength, int usedLength, ChannelContext channelContext) {
        // 小包消息处理
        if (channelContext.getOldByteBuffer().isEmpty()) {
            // 数据够用，直接读
            if (needLength <= buffer.buffer().remaining()) {
                byte[] bytes = new byte[needLength];
                buffer.buffer().get(bytes);
                return bytes;
            }
            // 数据不够
            return null;
        }
        // 大包消息处理，检查队列数据是否足够
        final int readBufferSize = channelContext.getAioConfig().getReadBufferSize();
        if (needLength + usedLength <= channelContext.getOldByteBuffer().size() * readBufferSize) {
            // 队列数据够，则读数据
            byte[] bytes = new byte[needLength];
            int index = 0;
            MemoryUnit oldBuffer;
            while ((oldBuffer = channelContext.getOldByteBuffer().poll()) != null && index <= needLength) {
                int relatable = Math.min(needLength - index, oldBuffer.buffer().remaining());
                oldBuffer.buffer().get(bytes, index, relatable);
                index += relatable;
                if (channelContext.getReadBuffer() != oldBuffer) {
                    oldBuffer.clean();
                }
            }
            return bytes;
        }
        // 若队列数据不够则继续读
        return null;
    }

    /**
     * 关闭用户通道
     *
     * @param asynchronousSocketChannel 用户通道
     */
    public static void closeImproveAsynchronousSocketChannel(ImproveAsynchronousSocketChannel asynchronousSocketChannel)
	{
		if (asynchronousSocketChannel == null) {
			return;
		}
        try {
			asynchronousSocketChannel.shutdownInput().shutdownOutput().close();
        } catch (NotYetConnectedException |IOException ignored) {
        }
    }
}
