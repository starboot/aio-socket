package cn.starboot.socket.core.tcp;

import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;

import java.io.IOException;
import java.nio.channels.NotYetConnectedException;

/**
 * TCP 模型专用工具类
 *
 * @author MDong
 */
final class TCPKernelUtils {

	/**
	 * 关闭用户通道
	 *
	 * @param asynchronousSocketChannel 用户通道
	 */
	static void closeImproveAsynchronousSocketChannel(ImproveAsynchronousSocketChannel asynchronousSocketChannel)
	{
		if (asynchronousSocketChannel == null) {
			return;
		}
		try {
			asynchronousSocketChannel.shutdownInput().shutdownOutput().close();
		} catch (NotYetConnectedException | IOException ignored) {
		}
	}
}
