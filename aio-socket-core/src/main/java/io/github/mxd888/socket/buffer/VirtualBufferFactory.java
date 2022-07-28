
package io.github.mxd888.socket.buffer;

/**
 * 创建虚拟ByteBuffer缓冲区的工厂
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface VirtualBufferFactory {

    /**
     * 在制定内存页内申请虚拟内存
     *
     * @param bufferPage  指定内存页
     * @return            虚拟内存
     */
    VirtualBuffer createBuffer(BufferPage bufferPage);

}
