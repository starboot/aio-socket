package io.github.mxd888.http.common.utils;

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
