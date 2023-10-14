/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.socket.plugins.ssl;


import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import javax.net.ssl.SSLEngine;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HandshakeModel {

    private AsynchronousSocketChannel socketChannel;
    private SSLEngine sslEngine;
    private MemoryUnit appWriteBuffer;
    private MemoryUnit netWriteBuffer;
    private MemoryUnit appReadBuffer;

    private MemoryUnit netReadBuffer;
    private HandshakeCallback handshakeCallback;
    private boolean eof;
    private boolean finished;

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public MemoryUnit getAppWriteBuffer() {
        return appWriteBuffer;
    }

    public void setAppWriteBuffer(MemoryUnit appWriteBuffer) {
        this.appWriteBuffer = appWriteBuffer;
    }

    public MemoryUnit getNetWriteBuffer() {
        return netWriteBuffer;
    }

    public void setNetWriteBuffer(MemoryUnit netWriteBuffer) {
        this.netWriteBuffer = netWriteBuffer;
    }

    public MemoryUnit getAppReadBuffer() {
        return appReadBuffer;
    }

    public void setAppReadBuffer(MemoryUnit appReadBuffer) {
        this.appReadBuffer = appReadBuffer;
    }

    public MemoryUnit getNetReadBuffer() {
        return netReadBuffer;
    }

    public void setNetReadBuffer(MemoryUnit netReadBuffer) {
        this.netReadBuffer = netReadBuffer;
    }

    public SSLEngine getSslEngine() {
        return sslEngine;
    }

    public void setSslEngine(SSLEngine sslEngine) {
        this.sslEngine = sslEngine;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public HandshakeCallback getHandshakeCallback() {
        return handshakeCallback;
    }

    public void setHandshakeCallback(HandshakeCallback handshakeCallback) {
        this.handshakeCallback = handshakeCallback;
    }

    public boolean isEof() {
        return eof;
    }

    public void setEof(boolean eof) {
        this.eof = eof;
    }

}
