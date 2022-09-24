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
package io.github.mxd888.socket.core;

import io.github.mxd888.socket.NetMonitor;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.utils.pool.buffer.BufferFactory;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.maintain.ClusterIds;
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
     * 启用AIO内核增强
     */
    private boolean enhanceCore = false;

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
     * 集群服务器：用户ID与集群服务器ID一一对应
     */
    private final ClusterIds clusterIds = new ClusterIds();

    /**
     * 是否启用插件
     */
    private boolean enablePlugins = false;

    /**
     * 是否启用心跳发送（客户端使用）
     */
    private Packet heartPacket;

    private int maxOnlineNum;

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

    public AioConfig setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
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

    public AioConfig setMonitor(NetMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public AioConfig setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    public Map<SocketOption<Object>, Object> getSocketOptions() {
        return socketOptions;
    }

    public AioConfig setOption(SocketOption<Object> socketOption, Object f) {
        if (socketOptions == null) {
            socketOptions = new HashMap<>(4);
        }
        socketOptions.put(socketOption, f);
        return this;
    }

    public AioConfig setSocketOptions(Map<SocketOption<Object>, Object> socketOptions) {
        this.socketOptions = socketOptions;
        return this;
    }

    public int getWriteBufferCapacity() {
        return writeBufferCapacity;
    }

    public AioConfig setWriteBufferCapacity(int writeBufferCapacity) {
        this.writeBufferCapacity = writeBufferCapacity;
        return this;
    }

    public int getBacklog() {
        return backlog;
    }

    public AioConfig setBacklog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public boolean isEnhanceCore() {
        return enhanceCore;
    }

    public AioConfig setEnhanceCore(boolean enhanceCore) {
        this.enhanceCore = enhanceCore;
        return this;
    }

    public boolean isServer() {
        return isServer;
    }

    public AioHandler getHandler() {
        return handler;
    }

    public AioConfig setHandler(AioHandler handler) {
        this.handler = handler;
        return this;
    }

    public BufferFactory getBufferFactory() {
        return bufferFactory;
    }

    public AioConfig setBufferFactory(BufferFactory bufferFactory) {
        this.bufferFactory = bufferFactory;
        return this;
    }

    public Groups getGroups() {
        return groups;
    }

    public Ids getIds() {
        return ids;
    }

    public ClusterIds getClusterIds() {
        return clusterIds;
    }

    public boolean isEnablePlugins() {
        return enablePlugins;
    }

    public AioConfig setEnablePlugins(boolean enablePlugins) {
        this.enablePlugins = enablePlugins;
        return this;
    }

    public Packet getHeartPacket() {
        return heartPacket;
    }

    public AioConfig setHeartPacket(Packet heartPacket) {
        this.heartPacket = heartPacket;
        return this;
    }

    public AioPlugins getPlugins() {
        return plugins;
    }

    public int getMaxOnlineNum() {
        return maxOnlineNum;
    }

    public void setMaxOnlineNum(int maxOnlineNum) {
        this.maxOnlineNum = maxOnlineNum;
    }
}
