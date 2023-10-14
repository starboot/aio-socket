/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface Handler<T> {

    /**
     * 解析 body 数据流
     *
     * @param buffer
     * @param request
     * @return
     */
    boolean onBodyStream(ByteBuffer buffer, T request);


    /**
     * Http header 完成解析
     */
    default void onHeaderComplete(T request) throws IOException {
    }

    /**
     * 断开 TCP 连接
     */
    default void onClose(T request) {
    }
}
