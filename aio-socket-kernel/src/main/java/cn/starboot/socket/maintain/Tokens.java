package cn.starboot.socket.maintain;

import cn.starboot.socket.core.ChannelContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Tokens {

	private final Map<String, Set<ChannelContext>> channelGroup = new ConcurrentHashMap<>();
}
