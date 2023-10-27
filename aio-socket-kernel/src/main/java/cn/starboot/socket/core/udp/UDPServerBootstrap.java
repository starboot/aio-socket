package cn.starboot.socket.core.udp;

import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.intf.AioHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UDP服务器引导程序
 *
 * @author MDong
 */
public final class UDPServerBootstrap extends UDPBootstrap {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDPServerBootstrap.class);

	public UDPServerBootstrap(AioHandler handler, AioConfig config) {
		super(handler, config);
	}
}
