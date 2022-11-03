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
package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.QuickTimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 服务器运行状态监控插件
 *
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
        QuickTimerTask.scheduleAtFixedRate(this, mills, mills);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's monitor plugin added successfully");
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
            default:
                //ignore other state
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
            LOGGER.debug("\r\n-----" + seconds + "seconds ----\r\ninflow:\t\t" + curInFlow * 1.0 / (1024 * 1024) + "(MB)"
                    + "\r\noutflow:\t" + curOutFlow * 1.0 / (1024 * 1024) + "(MB)"
                    + "\r\nprocess fail:\t" + curDiscardNum
                    + "\r\nprocess count:\t" + curProcessMsgNum
                    + "\r\nprocess total:\t" + totalProcessMsgNum
                    + "\r\nread count:\t" + curReadCount + "\twrite count:\t" + curWriteCount
                    + "\r\nconnect count:\t" + connectCount
                    + "\r\ndisconnect count:\t" + disConnectCount
                    + "\r\nonline count:\t" + onlineCount
                    + "\r\nconnected total:\t" + totalConnect
                    + "\r\nRequests/sec:\t" + curProcessMsgNum * 1.0 / seconds
                    + "\r\nTransfer/sec:\t" + (curInFlow * 1.0 / (1024 * 1024) / seconds) + "(MB)");
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
