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

import cn.starboot.socket.config.AioServerConfig;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousServerSocketChannel;
import cn.starboot.socket.jdk.aio.ImproveAsynchronousSocketChannel;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.plugins.Plugin;
import cn.starboot.socket.plugins.Plugins;
import cn.starboot.socket.utils.AIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.CompletionHandler;
import java.util.Map;

/**
 * AIO Server 启动类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ServerBootstrap extends AbstractBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerBootstrap.class);

	/**
	 * AIO Server 通道
	 */
	private ImproveAsynchronousServerSocketChannel serverSocketChannel;

	/**
	 * ServerBootstrap
	 *
	 * @param host    内网地址
	 * @param port    端口
	 * @param handler 任务处理器
	 */
	public ServerBootstrap(String host, int port, AioHandler handler) {
		super(new AioServerConfig());
		getConfig().setHost(host);
		getConfig().setPort(port);
		getConfig().getPlugins().addAioHandler(handler);
	}

	/**
	 * 启动AIO Socket Server
	 */
	public void start() {
		try {
			beforeStart();
		} catch (IOException e) {
			e.printStackTrace();
		}

		start0();
	}

	/**
	 * 内部启动逻辑
	 */
	private void start0() {
		try {
			checkAndResetConfig();
			this.serverSocketChannel = ImproveAsynchronousServerSocketChannel.open(getAsynchronousChannelGroup());
			if (getConfig().getSocketOptions() != null) {
				for (Map.Entry<SocketOption<Object>, Object> entry : getConfig().getSocketOptions().entrySet()) {
					this.serverSocketChannel.setOption(entry.getKey(), entry.getValue());
				}
			}
			if (getConfig().getHost() != null) {
				this.serverSocketChannel.bind(new InetSocketAddress(getConfig().getHost(), getConfig().getPort()), getConfig().getBacklog());
			} else {
				this.serverSocketChannel.bind(new InetSocketAddress(getConfig().getPort()), getConfig().getBacklog());
			}
			startAcceptThread();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("aio-socket version: {}; server kernel started successfully", AioConfig.VERSION);
			}
		} catch (IOException e) {
			shutdown();
			LOGGER.error("aio-socket version: {}; server kernel started failed", AioConfig.VERSION);
		}
	}

	/**
	 * 启动接受连接请求的监听
	 */
	private void startAcceptThread() {
		this.serverSocketChannel.accept(null, new CompletionHandler<ImproveAsynchronousSocketChannel, Void>() {
			@Override
			public void completed(ImproveAsynchronousSocketChannel channel, Void attachment) {
				try {
					serverSocketChannel.accept(attachment, this);
				} catch (Throwable throwable) {
					failed(throwable, attachment);
					serverSocketChannel.accept(attachment, this);
				} finally {
					initChannelContext(channel);
				}
			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				exc.printStackTrace();
			}
		});
	}


	/**
	 * 初始化每个链接通道
	 *
	 * @param channel 用户通道
	 */
	private void initChannelContext(ImproveAsynchronousSocketChannel channel) {

		//连接成功则构造ChannelContext对象
		TCPChannelContext context = null;
		ImproveAsynchronousSocketChannel acceptChannel = channel;
		try {
			if (getConfig().getMonitor() != null) {
				acceptChannel = getConfig().getMonitor().shouldAccept(channel);
			}
			if (acceptChannel != null) {
				acceptChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
				context = getBootstrapFunction().apply(acceptChannel);
				context.initTCPChannelContext();
			} else {
				assert channel != null;
				AIOUtil.closeImproveAsynchronousSocketChannel(channel);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (context == null) {
				AIOUtil.closeImproveAsynchronousSocketChannel(channel);
			} else {
				context.close(true);
			}
		}
	}

	/**
	 * 检查配置项
	 */
	private void checkAndResetConfig() {
		Plugins plugins = getConfig().getPlugins();
		getConfig().setMonitor(plugins)
				.setHandler(plugins);
		if (getConfig().getMaxOnlineNum() == 0) {
			// 默认单台aio-socket内核承载1000人，根据具体情况可以自由设置
			getConfig().setMaxOnlineNum(1000);
		}
	}

	/**
	 * 停止服务端
	 */
	public void shutdown() {
		try {
			if (this.serverSocketChannel != null) {
				this.serverSocketChannel.close();
				this.serverSocketChannel = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.shutdown();
	}

	/**
	 * 设置线程池线程数量
	 *
	 * @param bossThreadNum 内核IO线程数量
	 * @return ServerBootstrap 对象
	 */
	public ServerBootstrap setThreadNum(int bossThreadNum) {
		getConfig().setBossThreadNumber(bossThreadNum);
		return this;
	}

	/**
	 * 设置内存池工厂
	 *
	 * @param size      内存页大小
	 * @param num       内存页个数
	 * @param useDirect 是否开启堆外内存
	 * @return this
	 */
	public ServerBootstrap setMemoryPoolFactory(int size, int num, boolean useDirect) {
		getConfig().setDirect(useDirect).setMemoryBlockSize(size).setMemoryBlockNum(num);
		return this;
	}

	/**
	 * 设置写缓冲区大小
	 *
	 * @param writeBufferSize 写缓冲区大小
	 * @param maxWaitNum      最大等待队列长度
	 * @return ServerBootstrap 对象
	 */
	public ServerBootstrap setWriteBufferSize(int writeBufferSize, int maxWaitNum) {
		getConfig().setWriteBufferSize(writeBufferSize)
				.setMaxWaitNum(maxWaitNum);
		return this;
	}

	/**
	 * 设置读缓冲区大小
	 *
	 * @param readBufferSize 读缓冲区大小
	 * @return ServerBootstrap 对象
	 */
	public ServerBootstrap setReadBufferSize(int readBufferSize) {
		getConfig().setReadBufferSize(readBufferSize);
		return this;
	}

	public ServerBootstrap setMemoryKeep(boolean isMemoryKeep) {
		getConfig().setMemoryKeep(isMemoryKeep);
		return this;
	}

	/**
	 * 注册插件
	 *
	 * @param plugin 插件项
	 * @return ServerBootstrap 对象
	 */
	public ServerBootstrap addPlugin(Plugin plugin) {
		getConfig().getPlugins().addPlugin(plugin);
		return this;
	}

	/**
	 * 支持单端口绑定多协议
	 *
	 * @param aioHandler 协议处理器
	 * @return ServerBootstrap 对象
	 */
	public ServerBootstrap addAioHandler(AioHandler aioHandler) {
		getConfig().getPlugins().addAioHandler(aioHandler);
		return this;
	}

}
