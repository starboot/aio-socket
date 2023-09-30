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
package cn.starboot.socket.core;

import cn.starboot.socket.Monitor;
import cn.starboot.socket.maintain.*;
import cn.starboot.socket.utils.lock.SetWithLock;
import cn.starboot.socket.utils.pool.memory.MemoryPool;
import cn.starboot.socket.utils.pool.memory.MemoryPoolFactory;
import cn.starboot.socket.intf.Handler;
import cn.starboot.socket.plugins.Plugins;

import java.net.SocketOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * AIO 配置信息
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class AioConfig {

    /**
     * 当前aio-socket版本号
     */
    public static final String VERSION = "v3.0.0";

    /**
     * 消息体缓存大小,字节
     */
    private int readBufferSize = 2048;

    /**
     * 内存块大小限制
     */
    private int writeBufferSize = 2048;

    /**
     * 远程服务器IP
     */
    private String host;

    /**
     * 服务器消息拦截器
     */
    private Monitor monitor;

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
    private Handler handler;

	/**
	 * 单台aio-socket最大在线用户量；再启动MonitorPlugin时才生效（通过触发状态机来通知应用层处理）
	 */
	private int maxOnlineNum;

	private int maxWaitNum = 50;

	/**
	 * 启用用户连接保存
	 */
	private boolean useConnections;

	/**
	 * 是否启用零拷贝
	 */
	private boolean direct;

	private int memoryBlockNum;

	private int memoryBlockSize;

	private SetWithLock<ChannelContext> connections;

	/**
	 * 插件
	 */
	private final Plugins plugins = new Plugins();

    /**
     * Socket 配置
     */
    private Map<SocketOption<Object>, Object> socketOptions;

    /**
     * 内存池工厂
     */
    private MemoryPoolFactory memoryPoolFactory = MemoryPoolFactory.DISABLED_BUFFER_FACTORY;

	/**
	 * 关系维持管理器
	 */
	private final MaintainManager maintainManager = MaintainManager.getInstance();

    public int getMaxWaitNum() {
        return maxWaitNum;
    }

    public void setMaxWaitNum(int maxWaitNum) {
        this.maxWaitNum = maxWaitNum;
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

    public Monitor getMonitor() {
        return monitor;
    }

    public AioConfig setMonitor(Monitor monitor) {
        this.monitor = monitor;
        return this;
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

    public int getBacklog() {
        return backlog;
    }

    public AioConfig setBacklog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public Handler getHandler() {
        return handler;
    }

    public AioConfig setHandler(Handler handler) {
        this.handler = handler;
        return this;
    }

    public MemoryPoolFactory getMemoryPoolFactory() {
        return memoryPoolFactory;
    }

    public void setMemoryPoolFactory(MemoryPoolFactory memoryPoolFactory) {
        this.memoryPoolFactory = memoryPoolFactory;
    }

    protected void initMemoryPoolFactory() {
    	this.memoryPoolFactory = () -> new MemoryPool(getMemoryBlockSize(), getMemoryBlockNum(), isDirect());
	}

    public Plugins getPlugins() {
        return plugins;
    }

    public int getMaxOnlineNum() {
        return maxOnlineNum;
    }

    public void setMaxOnlineNum(int maxOnlineNum) {
        this.maxOnlineNum = maxOnlineNum;
    }

	protected MaintainManager getMaintainManager() {
		return this.maintainManager;
	}

	public boolean isUseConnections() {
		return this.useConnections;
	}

	public synchronized void setUseConnections(boolean useConnections) {
		this.useConnections = useConnections;
		if (this.useConnections && Objects.isNull(this.connections)) {
			this.connections = new SetWithLock<>(new HashSet<>());
		}
	}

	public SetWithLock<ChannelContext> getConnections() {
		return connections;
	}

	public boolean isDirect() {
		return direct;
	}

	public AioConfig setDirect(boolean direct) {
		this.direct = direct;
		return this;
	}

	public int getMemoryBlockNum() {
		return memoryBlockNum;
	}

	public void setMemoryBlockNum(int memoryBlockNum) {
		this.memoryBlockNum = memoryBlockNum;
	}

	public int getMemoryBlockSize() {
		return memoryBlockSize;
	}

	public AioConfig setMemoryBlockSize(int memoryBlockSize) {
		this.memoryBlockSize = memoryBlockSize;
		return this;
	}

	public abstract String getName();

	public abstract boolean isServer();

	public abstract AioConfig setBossThreadNumber(int bossThreadNumber);

	public abstract void setWorkerThreadNumber(int workerThreadNumber);

	public abstract int getBossThreadNumber();

	public abstract int getWorkerThreadNumber();

	@Override
	public String toString() {
		return "AioConfig{" +
				", serverName=" + getName() +
				", isServer=" + isServer() +
				", readBufferSize=" + readBufferSize +
				", writeBufferSize=" + writeBufferSize +
				", host='" + host + '\'' +
				", port=" + port +
				", backlog=" + backlog +
				", maxOnlineNum=" + maxOnlineNum +
				", maxWaitNum=" + maxWaitNum +
				", useConnections=" + useConnections +
				", direct=" + direct +
				", memoryBlockNum=" + memoryBlockNum +
				", memoryBlockSize=" + memoryBlockSize +
				", bossThreadNum=" + getBossThreadNumber() +
				", workerThreadNum=" + getWorkerThreadNumber() +
				'}';
	}
}
