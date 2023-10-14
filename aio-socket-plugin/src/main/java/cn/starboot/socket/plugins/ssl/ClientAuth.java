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
 * 配置引擎请求客户端验证。此选项只对服务器模式的引擎有用
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum ClientAuth {
    /**
     * 不需要客户端验证
     */
    NONE,
    /**
     * 请求的客户端验证
     * 如果设置了此选项并且客户端选择不提供其自身的验证信息，则协商将会继续
     */
    OPTIONAL,
    /**
     * 必须的客户端验证
     * 如果设置了此选项并且客户端选择不提供其自身的验证信息，则协商将会停止且引擎将开始它的关闭过程
     */
    REQUIRE
}
