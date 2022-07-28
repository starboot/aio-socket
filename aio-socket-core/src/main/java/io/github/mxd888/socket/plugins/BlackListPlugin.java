package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.core.AioConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 黑名单插件,smart-socket会拒绝与黑名单中的IP建立连接
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class BlackListPlugin extends AbstractPlugin {

    private final ConcurrentLinkedQueue<BlackListRule> ipBlackList = new ConcurrentLinkedQueue<>();

    public BlackListPlugin() {
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's black list plugin added successfully");
    }

    @Override
    public AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        InetSocketAddress inetSocketAddress = null;
        try {
            inetSocketAddress = (InetSocketAddress) channel.getRemoteAddress();
        } catch (IOException e) {
            System.out.println("get remote address error " + e);
        }
        if (inetSocketAddress == null) {
            return channel;
        }
        for (BlackListRule rule : ipBlackList) {
            if (!rule.access(inetSocketAddress)) {
                return null;
            }
        }
        return channel;
    }

    /**
     * 添加黑名单失败规则
     *
     * @param rule .
     */
    public void addRule(BlackListRule rule) {
        ipBlackList.add(rule);
    }

    /**
     * 移除黑名单规则
     *
     * @param rule .
     */
    public void removeRule(BlackListRule rule) {
        ipBlackList.remove(rule);
    }

    /**
     * 黑名单规则定义
     */
    public interface BlackListRule {
        /**
         * 是否允许建立连接
         *
         * @param address .
         * @return .
         */
        boolean access(InetSocketAddress address);
    }
}
