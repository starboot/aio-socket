/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.utils;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface SmartDecoder {
    /**
     * 解码算法
     *
     * @param byteBuffer
     * @return
     */
    boolean decode(ByteBuffer byteBuffer);

    /**
     * 获取本次解析到的完整数据
     *
     * @return
     */
    ByteBuffer getBuffer();
}
