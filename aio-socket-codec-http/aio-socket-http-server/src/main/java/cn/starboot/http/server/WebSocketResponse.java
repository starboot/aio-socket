/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface WebSocketResponse {
    /**
     * 发送文本响应
     *
     * @param text
     */
    void sendTextMessage(String text);

    /**
     * 发送二进制响应
     *
     * @param bytes
     */
    void sendBinaryMessage(byte[] bytes);
    /**
     * 发送二进制响应
     *
     * @param bytes
     */
    void sendBinaryMessage(byte[] bytes,int offset, int length);

    /**
     * 关闭ws通道
     */
    void close();

    /**
     * 输出数据
     */
    void flush();
}
