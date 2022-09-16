package io.github.mxd888.socket.utils;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.NotYetConnectedException;

/**
 * IO工具包
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class IOUtil {

    /**
     * 关闭用户通道
     *
     * @param channel 用户通道
     */
    public static void close(AsynchronousSocketChannel channel) {
        boolean connected = true;
        try {
            channel.shutdownInput();
        } catch (IOException ignored) {
        } catch (NotYetConnectedException e) {
            connected = false;
        }
        try {
            if (connected) {
                channel.shutdownOutput();
            }
        } catch (IOException | NotYetConnectedException ignored) {
        }
        try {
            channel.close();
        } catch (IOException ignored) {
        }
    }
}
