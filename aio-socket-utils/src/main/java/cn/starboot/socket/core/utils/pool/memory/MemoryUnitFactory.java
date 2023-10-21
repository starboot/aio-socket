/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.socket.core.utils.pool.memory;

/**
 * 创建虚拟ByteBuffer缓冲区的工厂
 *
 * @author smart-socket: https://gitee.com/smartboot/smart-socket.git
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface MemoryUnitFactory {

    /**
     * 在制定内存页内申请虚拟内存
     *
     * @param memoryBlock  指定内存页
     * @return            虚拟内存
     */
    MemoryUnit createMemoryUnit(MemoryBlock memoryBlock);

}
