package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.intf.AioHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UDPClientBootstrap extends UDPBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPClientBootstrap.class);

	public UDPClientBootstrap(AioHandler handler, AioConfig config) {
		super(handler, config);
	}
}
