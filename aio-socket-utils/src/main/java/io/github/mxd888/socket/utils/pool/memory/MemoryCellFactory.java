package io.github.mxd888.socket.utils.pool.memory;

/**
 * 内存单元工厂
 * 1、生产内存单元
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface MemoryCellFactory {

    /**
     * 在制定内存页内申请虚拟内存
     *
     * @param memoryChunk  指定内存页
     * @return             虚拟内存
     */
    MemoryCell createMemoryCell(MemoryChunk memoryChunk);
}
