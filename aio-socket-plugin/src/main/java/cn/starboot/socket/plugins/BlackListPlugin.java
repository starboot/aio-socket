package cn.starboot.socket.plugins;

import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

/**
 * 黑名单插件,smart-socket会拒绝与黑名单中的IP建立连接
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class BlackListPlugin extends AbstractPlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlackListPlugin.class);

    private final ConcurrentLinkedQueue<Predicate<InetSocketAddress>> ipBlackList = new ConcurrentLinkedQueue<>();

    public BlackListPlugin() {
        System.out.println("aio-socket version: " + AioConfig.VERSION + "; server kernel's black list plugin added successfully");
    }

    @Override
    public ImproveAsynchronousSocketChannel agreeAccept(ImproveAsynchronousSocketChannel asynchronousSocketChannel) {
        InetSocketAddress inetSocketAddress = null;
        try {
            inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
        } catch (IOException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage());
			}
        }
        if (inetSocketAddress == null) {
            return asynchronousSocketChannel;
        }
        for (Predicate<InetSocketAddress> predicate : ipBlackList) {
            if (!predicate.test(inetSocketAddress)) {
                return null;
            }
        }
		return asynchronousSocketChannel;
    }

    /**
     * 添加黑名单失败规则
     *
     * @param predicate .
     */
    public void addRule(Predicate<InetSocketAddress> predicate) {
        ipBlackList.add(predicate);
    }

    /**
     * 移除黑名单规则
     *
     * @param predicate .
     */
    public void removeRule(Predicate<InetSocketAddress> predicate) {
        ipBlackList.remove(predicate);
    }
}
