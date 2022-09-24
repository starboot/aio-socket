package io.github.mxd888.socket.utils.pool.memory;

/**
 * 内存池工厂
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
