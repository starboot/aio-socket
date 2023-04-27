package cn.starboot.socket.core;

/**
 * 过滤器
 *
 * @author MDong
 */
public interface ChannelContextFilter {

	/**
	 * 过滤方法
	 * 假设过滤出群组内出自己外所有人，自己的上下文信息为：userChannelContext
	 * 则使用如下写法：
	 *		channelContext -> {channelContext != userChannelContext}
	 *
	 * @param channelContext 待判断的通道上下文信息
	 * @return 是否满足保留条件
	 */
	boolean filter(ChannelContext channelContext);
}
