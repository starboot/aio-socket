package io.github.mxd888.socket.core;

import io.github.mxd888.socket.NetMonitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.buffer.BufferFactory;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.maintain.Groups;
import io.github.mxd888.socket.maintain.Ids;
import io.github.mxd888.socket.plugins.AioPlugins;

import java.net.SocketOption;
import java.util.HashMap;
import java.util.Map;

/**
 * AIO 配置信息
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class AioConfig {

    /**
     * 当前aio-socket版本号
     */
    public static final String VERSION = "2.10.1.v20211002-RELEASE";

    /**
     * 消息体缓存大小,字节
     */
    private int readBufferSize = 2048;

    /**
     * 内存块大小限制
     */
    private int writeBufferSize = 2048;

    /**
     * Write缓存区容量
     */
    private int writeBufferCapacity = 16;

    /**
     * 远程服务器IP
     */
    private String host;

    /**
     * 服务器消息拦截器
     */
    private NetMonitor monitor;

    /**
     * 服务器端口号
     */
    private int port;

    /**
     * 服务端backlog
     */
    private int backlog = 1000;

    /**
     * 消息处理器
     */
    private AioHandler handler;

    /**
     * Socket 配置
     */
    private Map<SocketOption<Object>, Object> socketOptions;

    /**
     * 内存池工厂
     */
    private BufferFactory bufferFactory = BufferFactory.ENABLE_BUFFER_FACTORY;

    /**
     * 是否是服务器
     */
    private boolean isServer;

    /**
     * 群组和其ChannelContext绑定
     */
    private final Groups groups = new Groups();

    /**
     * 用户ID和其ChannelContext绑定
     */
    private final Ids ids = new Ids();

    /**
     * 是否启用插件
     */
    private boolean enablePlugins = false;

    /**
     * 是否启用心跳发送（客户端使用）
     */
    private Packet heartPacket;

    /**
     * 插件
     */
    private final AioPlugins plugins = new AioPlugins();

    public AioConfig(boolean isServer) {
        this.isServer = isServer;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NetMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(NetMonitor monitor) {
        this.monitor = monitor;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public Map<SocketOption<Object>, Object> getSocketOptions() {
        return socketOptions;
    }

    public void setOption(SocketOption<Object> socketOption, Object f) {
        if (socketOptions == null) {
            socketOptions = new HashMap<>(4);
        }
        socketOptions.put(socketOption, f);
    }

    public void setSocketOptions(Map<SocketOption<Object>, Object> socketOptions) {
        this.socketOptions = socketOptions;
    }

    public int getWriteBufferCapacity() {
        return writeBufferCapacity;
    }

    public void setWriteBufferCapacity(int writeBufferCapacity) {
        this.writeBufferCapacity = writeBufferCapacity;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public AioHandler getHandler() {
        return handler;
    }

    public void setHandler(AioHandler handler) {
        this.handler = handler;
    }

    public BufferFactory getBufferFactory() {
        return bufferFactory;
    }

    public void setBufferFactory(BufferFactory bufferFactory) {
        this.bufferFactory = bufferFactory;
    }

    public Groups getGroups() {
        return groups;
    }

    public Ids getIds() {
        return ids;
    }

    public boolean isEnablePlugins() {
        return enablePlugins;
    }

    public void setEnablePlugins(boolean enablePlugins) {
        this.enablePlugins = enablePlugins;
    }

    public Packet getHeartPacket() {
        return heartPacket;
    }

    public void setHeartPacket(Packet heartPacket) {
        this.heartPacket = heartPacket;
    }

    public AioPlugins getPlugins() {
        return plugins;
    }

}
