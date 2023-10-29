package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.functional.MemoryUnitSupplier;
import cn.starboot.socket.core.jdk.nio.ImproveNioSelector;
import cn.starboot.socket.core.jdk.nio.NioEventLoopWorker;
import cn.starboot.socket.core.utils.concurrent.map.ConcurrentWithMap;
import cn.starboot.socket.core.utils.pool.memory.MemoryBlock;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;
import cn.starboot.socket.core.utils.pool.memory.MemoryUnitFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.function.Consumer;

final class UDPReadWorker {

	private final DatagramChannel serverDatagramChannel;

	private final NioEventLoopWorker nioEventLoopWorker;

	private final AioConfig aioConfig;

	private final MemoryUnitSupplier readMemoryUnitSupplier;

	private final ConcurrentWithMap<SocketAddress, UDPChannelContext> channelContextHashMap = new ConcurrentWithMap<>(new HashMap<>());

	static NioEventLoopWorker openUDPReadWorker(DatagramChannel serverDatagramChannel,
												AioConfig aioConfig,
												MemoryUnitSupplier readMemoryUnitSupplier) {
		return new UDPReadWorker(serverDatagramChannel, aioConfig, readMemoryUnitSupplier).nioEventLoopWorker;
	}

	private UDPReadWorker(DatagramChannel serverDatagramChannel,
						  AioConfig aioConfig,
						  MemoryUnitSupplier readMemoryUnitSupplier) {
		this.serverDatagramChannel = serverDatagramChannel;
		this.aioConfig = aioConfig;
		this.readMemoryUnitSupplier = readMemoryUnitSupplier;
		this.nioEventLoopWorker = new NioEventLoopWorker(ImproveNioSelector.open(), new Consumer<SelectionKey>() {
			@Override
			public void accept(SelectionKey selectionKey) {
				handle0(selectionKey);
			}
		});
	}

	private void handle0(SelectionKey selectionKey) {
		if (selectionKey.isReadable()) {
			DatagramChannel channel = (DatagramChannel) selectionKey.channel();
			try {
				// 申请内存
				final MemoryUnit readMemoryUnit = readMemoryUnitSupplier.applyMemoryUnit();
				readMemoryUnit.buffer().clear();
				SocketAddress receive = channel.receive(readMemoryUnit.buffer());
				channelContextHashMap.containsKey(receive, new Consumer<Boolean>() {
					@Override
					public void accept(Boolean aBoolean) {
						if (aBoolean) {
							channelContextHashMap.get(receive, new Consumer<UDPChannelContext>() {
								@Override
								public void accept(UDPChannelContext udpChannelContext) {
									udpChannelContext.addMemoryUnit(readMemoryUnit).handle();
								}
							});
						} else {
							channelContextHashMap.put(
									receive,
									new UDPChannelContext(serverDatagramChannel,
											aioConfig,
											receive,
											null,
											nioEventLoopWorker),
									new Consumer<UDPChannelContext>() {
										@Override
										public void accept(UDPChannelContext udpChannelContext) {
											udpChannelContext.addMemoryUnit(readMemoryUnit).handle();
										}
									});
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (selectionKey.isWritable()) {
			selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
			UDPChannelContext attachment = (UDPChannelContext) selectionKey.attachment();
			attachment.doWrite();
		}
	}
}
