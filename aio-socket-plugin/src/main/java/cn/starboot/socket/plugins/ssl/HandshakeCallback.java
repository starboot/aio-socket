/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.socket.plugins.ssl;

/**
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
interface HandshakeCallback {
    /**
     * 握手回调
     */
    void callback();
}
