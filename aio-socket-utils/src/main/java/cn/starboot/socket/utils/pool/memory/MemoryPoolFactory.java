/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.socket.utils.pool.memory;

/**
 * 内存池工厂
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface MemoryPoolFactory {

    /**
     * 禁用状态的内存池
     */
    MemoryPoolFactory DISABLED_BUFFER_FACTORY = () -> new MemoryPool(0, 1, false);

    /**
     * 创建内存池
     *
     * @return 生成的内存池对象
     */
    MemoryPool create();
}
