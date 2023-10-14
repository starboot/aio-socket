/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server;

import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HeaderValueEnum;
import cn.starboot.http.common.enums.HttpMethodEnum;
import cn.starboot.http.common.enums.HttpProtocolEnum;

import cn.starboot.http.server.banner.HttpBanner;
import cn.starboot.http.server.impl.HttpMessageProcessor;
import cn.starboot.http.server.impl.HttpRequestHandler;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpBootstrap.class);

	/* 端口号 */
	private int port = 8080;

	/* aio-socket 服务器 */
	private ServerBootstrap server;

	/* http消息处理器 */
    private final HttpMessageProcessor processor;

	/* http服务器配置信息 */
    private final HttpServerConfiguration configuration = new HttpServerConfiguration();

    public HttpBootstrap() {
		this.processor = new HttpMessageProcessor();
		this.processor.setConfiguration(configuration);
    }

    /**
     * Http服务端口号
     */
    public HttpBootstrap setPort(int port) {
        this.port = port;
        return this;
    }

	/**
	 * 注册handle
	 *
	 * @param serverHandler 处理器
	 * @return HTTP服务器
	 */
	public HttpBootstrap addHandler(ServerHandler<?, ?> serverHandler) {
		if (serverHandler instanceof HttpServerHandler) {
			processor.httpServerHandler((HttpServerHandler) serverHandler);
		}else if (serverHandler instanceof WebSocketHandler) {
			processor.setWebSocketHandler((WebSocketHandler) serverHandler);
		}
    	return this;
	}

    /**
     * 服务配置
     *
     * @return 配置类
     */
    public HttpServerConfiguration configuration() {
        return configuration;
    }

    /**
     * 启动HTTP服务
     */
    public void start() {
        initByteCache();
        configuration.getPlugins().forEach(requestPlugin -> server.getConfig().getPlugins().addPlugin(requestPlugin));
        server = new ServerBootstrap(configuration.getHost(), port, new HttpRequestHandler(configuration, processor));
        if (configuration.isBannerEnabled() && LOGGER.isInfoEnabled()) {
			LOGGER.info("\r\n" + HttpBanner.BANNER + "\r\n :: aio-socket http server :: (" + AioConfig.VERSION + ")");
        }
        server.setReadBufferSize(configuration.getReadPageSize())
				// 不知道为什么  HTTP 不支持直接内存 也就是零拷贝
//				.setMemoryPoolFactory(16 * 1024 * 1024, 10, false)
				.start();
    }

    private void updateHeaderNameByteTree() {
        configuration.getHeaderNameByteTree().addNode(HeaderNameEnum.UPGRADE.getName(), upgrade -> {
            // WebSocket
            if (HeaderValueEnum.WEBSOCKET.getName().equals(upgrade)) {
                return configuration.getWebSocketHandler();
            }
            // HTTP/2.0
            else if (HeaderValueEnum.H2C.getName().equals(upgrade) || HeaderValueEnum.H2.getName().equals(upgrade)) {
                return new Http2ServerHandler() {
                    @Override
                    public void handle(HttpRequest request, HttpResponse response) throws IOException {
                        configuration.getHttpServerHandler().handle(request, response);
                    }

                    @Override
                    public void handle(HttpRequest request, HttpResponse response, CompletableFuture<Object> completableFuture) throws IOException {
                        configuration.getHttpServerHandler().handle(request, response, completableFuture);
                    }
                };
            } else {
                return null;
            }
        });
    }

    private void initByteCache() {
        for (HttpMethodEnum httpMethodEnum : HttpMethodEnum.values()) {
            configuration.getByteCache().addNode(httpMethodEnum.getMethod());
        }
        for (HttpProtocolEnum httpProtocolEnum : HttpProtocolEnum.values()) {
            configuration.getByteCache().addNode(httpProtocolEnum.getProtocol());
        }
        for (HeaderNameEnum headerNameEnum : HeaderNameEnum.values()) {
            configuration.getHeaderNameByteTree().addNode(headerNameEnum.getName());
        }
        for (HeaderValueEnum headerValueEnum : HeaderValueEnum.values()) {
            configuration.getByteCache().addNode(headerValueEnum.getName());
        }

        updateHeaderNameByteTree();
    }

    /**
     * 停止服务
     */
    public void shutdown() {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }
}
