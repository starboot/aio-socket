package io.github.mxd888.http.common;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by DELL(mxd) on 2022/7/28 18:19
 */
public interface Handler<T> {

    /**
     * 解析 body 数据流
     *
     * @param buffer
     * @param request
     * @return
     */
    boolean onBodyStream(ByteBuffer buffer, T request);


    /**
     * Http header 完成解析
     */
    default void onHeaderComplete(T request) throws IOException {
    }

    /**
     * 断开 TCP 连接
     */
    default void onClose(T request) {
    }
}
