/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.enums;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum HttpTypeEnum {

    /**
     * 普通http消息
     */
    HTTP,

    /**
     * websocket消息
     */
    WEBSOCKET,

    /**
     * Http2.0消息
     */
    HTTP_2
	;
}
