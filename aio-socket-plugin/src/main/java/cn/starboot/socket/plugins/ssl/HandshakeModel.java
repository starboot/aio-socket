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
package cn.starboot.socket.plugins.ssl;


import cn.starboot.socket.utils.pool.memory.MemoryUnit;

import javax.net.ssl.SSLEngine;
import java.nio.channels.AsynchronousSocketChannel;

/**
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
