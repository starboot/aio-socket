package cn.starboot.http.server;

import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HeaderValueEnum;
import cn.starboot.http.common.enums.HttpMethodEnum;
import cn.starboot.http.common.enums.HttpProtocolEnum;
import cn.starboot.http.server.impl.HttpMessageProcessor;
import cn.starboot.http.server.impl.HttpRequestHandler;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.core.ServerBootstrap;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpBootstrap {

    private static final String BANNER = "  _      _    _    _          \n" +
            " ( )    ( )_ ( )_        \n" +
            " | |__  | ,_)| ,_) _ _   \n" +
            " |  _ `\\| |  | |  ( '_`\\ \n" +
            " | | | || |_ | |_ | (_) )\n" +
            " (_) (_)`\\__)`\\__)| ,__/'\n" +
            "                  | |    \n" +
            "                  (_)   ";

    private static final String VERSION = "1.1.16-SNAPSHOT";
    /**
     * http消息解码器
     */
    private final HttpMessageProcessor processor;
    private final HttpServerConfiguration configuration = new HttpServerConfiguration();
    private ServerBootstrap server;
    /**
     * Http服务端口号
     */
    private int port = 8080;


    public HttpBootstrap() {
        this(new HttpMessageProcessor());
    }

    public HttpBootstrap(HttpMessageProcessor processor) {
        this.processor = processor;
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
     * 往 http 处理器管道中注册 Handle
     *
     * @param httpHandler
     * @return
     */
    public HttpBootstrap httpHandler(HttpServerHandler httpHandler) {
        processor.httpServerHandler(httpHandler);
        return this;
    }

    /**
     * 获取websocket的处理器管道
     *
     * @return
     */
    public HttpBootstrap webSocketHandler(WebSocketHandler webSocketHandler) {
        processor.setWebSocketHandler(webSocketHandler);
        return this;
    }

    /**
     * 服务配置
     *
     * @return
     */
    public HttpServerConfiguration configuration() {
        return configuration;
    }

    /**
     * 启动HTTP服务
     */
    public void start() {
        initByteCache();
        MemoryPool readBufferPool = new MemoryPool(configuration.getReadPageSize(), 1, false);
        configuration.getPlugins().forEach(requestPlugin -> server.getConfig().getPlugins().addPlugin(requestPlugin));

        server = new ServerBootstrap(configuration.getHost(), port, new HttpRequestHandler(configuration, processor));
        if (configuration.isBannerEnabled()) {
            System.out.println(BANNER + "\r\n :: aio-socket-http :: (" + VERSION + ")");
        }
        server.start();
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
