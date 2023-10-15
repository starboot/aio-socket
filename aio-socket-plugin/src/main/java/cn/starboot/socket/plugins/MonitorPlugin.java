/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.socket.plugins;

import cn.starboot.socket.Packet;
import cn.starboot.socket.enums.StateMachineEnum;
import cn.starboot.socket.core.AioConfig;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.utils.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 服务器运行状态监控插件
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class MonitorPlugin extends AbstractPlugin implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorPlugin.class);

	/**
	 * 当前周期内流入字节数
	 */
	private final LongAdder inFlow = new LongAdder();

	/**
	 * 当前周期内流出字节数
	 */
	private final LongAdder outFlow = new LongAdder();

	/**
	 * 当前周期内处理失败消息数
	 */
	private final LongAdder processFailNum = new LongAdder();

	/**
	 * 当前周期内处理消息数
	 */
	private final LongAdder processMsgNum = new LongAdder();

	/**
	 * 当前周期内新建连接数
	 */
	private final LongAdder newConnect = new LongAdder();

	/**
	 * 当前周期内断开连接数
	 */
	private final LongAdder disConnect = new LongAdder();

	/**
	 * 当前周期内执行 read 操作次数
	 */
	private final LongAdder readCount = new LongAdder();

	/**
	 * 当前周期内执行 write 操作次数
	 */
	private final LongAdder writeCount = new LongAdder();

	/**
	 * 任务执行频率
	 */
	private final int seconds;

	/**
	 * 自插件启用起的累计连接总数
	 */
	private long totalConnect;

	/**
	 * 自插件启用起的累计处理消息总数
	 */
	private long totalProcessMsgNum = 0;

	/**
	 * 当前在线状态连接数
	 */
	private long onlineCount;

	public MonitorPlugin() {
		this(60);
	}

	public MonitorPlugin(int seconds) {
		this.seconds = seconds;
		long mills = TimeUnit.SECONDS.toMillis(seconds);
		TimerService.scheduleAtFixedRate(this, mills, mills);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("aio-socket version: " + AioConfig.VERSION + "; server kernel's monitor plugin added successfully");
		}
	}


	@Override
	public boolean beforeProcess(ChannelContext channelContext, Packet packet) {
		processMsgNum.increment();
		return true;
	}

	@Override
	public void stateEvent(StateMachineEnum stateMachineEnum, ChannelContext channelContext, Throwable throwable) {
		switch (stateMachineEnum) {
			case PROCESS_EXCEPTION:
				processFailNum.increment();
				break;
			case NEW_CHANNEL:
				AioConfig config = channelContext.getAioConfig();
				if (newConnect.longValue() > config.getMaxOnlineNum()) {
					config.getHandler().stateEvent(channelContext, StateMachineEnum.REJECT_ACCEPT, throwable);
				}
				newConnect.increment();
				break;
			case CHANNEL_CLOSED:
				disConnect.increment();
				break;
		}
	}

	@Override
	public void run() {
		long curInFlow = getAndReset(inFlow);
		long curOutFlow = getAndReset(outFlow);
		long curDiscardNum = getAndReset(processFailNum);
		long curProcessMsgNum = getAndReset(processMsgNum);
		long connectCount = getAndReset(newConnect);
		long disConnectCount = getAndReset(disConnect);
		long curReadCount = getAndReset(readCount);
		long curWriteCount = getAndReset(writeCount);
		onlineCount += connectCount - disConnectCount;
		totalProcessMsgNum += curProcessMsgNum;
		totalConnect += connectCount;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("\r\n------------------------------------------------"
					+ "\r\n\t\t\taio-socket performance"
					+ "\r\n-------------------in " + seconds + " sec --------------------"
					+ "\r\n输入流量:\t\t\t" + curInFlow * 1.0 / (1024 * 1024) + "(MB)"
					+ "\r\n输出流量:\t\t\t" + curOutFlow * 1.0 / (1024 * 1024) + "(MB)"
					+ "\r\n处理失败:\t\t\t" + curDiscardNum
					+ "\r\n实时处理:\t\t\t" + curProcessMsgNum
					+ "\r\n总处理量:\t\t\t" + totalProcessMsgNum
					+ "\r\n读取次数:\t\t\t" + curReadCount
					+ "\r\n写入次数:\t\t\t" + curWriteCount
					+ "\r\n实时连接:\t\t\t" + connectCount
					+ "\r\n断开连接:\t\t\t" + disConnectCount
					+ "\r\n在线人数:\t\t\t" + onlineCount
					+ "\r\n连接总量:\t\t\t" + totalConnect
					+ "\r\n单位消息:\t\t\t" + curProcessMsgNum * 1.0 / seconds + " (条/s)"
					+ "\r\n单位流量:\t\t\t" + (curInFlow * 1.0 / (1024 * 1024) / seconds) + " (MB/s)"
					+ "\r\n------------------------------------------------");
		}
	}

	private long getAndReset(LongAdder longAdder) {
		long result = longAdder.longValue();
		longAdder.add(-result);
		return result;
	}

	@Override
	public void afterRead(ChannelContext channelContext, int readSize) {
		//出现result为0,说明代码存在问题
		if (readSize == 0) {
			System.err.println("readSize is 0");
		}
		inFlow.add(readSize);
	}

	@Override
	public void beforeRead(ChannelContext channelContext) {
		readCount.increment();
	}

	@Override
	public void afterWrite(ChannelContext channelContext, int writeSize) {
		outFlow.add(writeSize);
	}

	@Override
	public void beforeWrite(ChannelContext channelContext) {
		writeCount.increment();
	}
}
