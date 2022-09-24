
package io.github.mxd888.socket.utils.pool.buffer;

/**
 * 内存池工厂
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface BufferFactory {

    /**
     * 禁用状态的内存池
     */
    BufferFactory DISABLED_BUFFER_FACTORY = () -> new BufferPagePool(0, 1, false);

    /**
     * 可用状态的内存池  这里 5120 2 false，表示有两个内存页可申请  每个5KB * 2 = 20   10GB = 5KB * 2  * 1024 * 1024 = 20 * 1024 *1024 = 20971520个用户  30w用户 = 300MB
     */
    BufferFactory ENABLE_BUFFER_FACTORY = () -> new BufferPagePool(5 * 1024 * 1024 * 2, 2, false);

    /**
     * 创建内存池
     *
     * @return 生成的内存池对象
     */
    BufferPagePool create();
}
