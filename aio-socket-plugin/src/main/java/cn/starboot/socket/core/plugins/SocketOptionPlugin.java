/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.socket.core.plugins;

import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.jdk.aio.ImproveAsynchronousSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketOption;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于设置Socket Option的插件
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class SocketOptionPlugin extends AbstractPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectPlugin.class);

    private final Map<SocketOption<Object>, Object> optionMap = new HashMap<>();

    public SocketOptionPlugin() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("aio-socket version: " + AioConfig.VERSION + "; server kernel's stream socket option plugin added successfully");
        }
    }

//    @Override
//    public final AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
//        setOption(channel);
//        return super.shouldAccept(channel);
//    }

    /**
     * 往socket channel中设置option值。
     * 默认将通过{@link #setOption(SocketOption, Object)}指定的配置值绑定到每一个Socket中。
     * 如果有个性化的需求,可以重新实现本方法。
     *
     * @param asynchronousSocketChannel .
     */
    public void setOption(ImproveAsynchronousSocketChannel asynchronousSocketChannel) {
//        try {
//            if (!optionMap.containsKey(StandardSocketOptions.TCP_NODELAY)) {
//				asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
//            }
//            for (Map.Entry<SocketOption<Object>, Object> entry : optionMap.entrySet()) {
//				asynchronousSocketChannel.setOption(entry.getKey(), entry.getValue());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 设置Socket的TCP参数配置。
     * <p>
     * AIO客户端的有效可选范围为：
     * 1. StandardSocketOptions.SO_SNDBUF
     * 2. StandardSocketOptions.SO_RCVBUF
     * 3. StandardSocketOptions.SO_KEEPALIVE
     * 4. StandardSocketOptions.SO_REUSEADDR
     * 5. StandardSocketOptions.TCP_NODELAY
     * </p>
     *
     * @param socketOption 配置项
     * @param value        配置值
     * @return .
     */
    public final SocketOptionPlugin setOption(SocketOption<Object> socketOption, Object value) {
        put0(socketOption, value);
        return this;
    }

    public final Object getOption(SocketOption<Object> socketOption) {
        return optionMap.get(socketOption);
    }

    private void put0(SocketOption<Object> socketOption, Object value) {
        optionMap.put(socketOption, value);
    }
}
