/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server;

import cn.starboot.http.common.Handler;
import cn.starboot.http.server.impl.HttpRequestPacket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface ServerHandler<REQ, RSP> extends Handler<HttpRequestPacket> {

    /**
     * 执行当前处理器逻辑。
     * <p>
     * 当前handle运行完后若还有后续的处理器，需要调用doNext
     * </p>
     *
     * @param request
     * @param response
     * @throws IOException
     */
    default void handle(REQ request, RSP response) throws IOException {
    }

    default void handle(REQ request, RSP response, CompletableFuture<Object> completableFuture) throws IOException {
        try {
            handle(request, response);
        } finally {
            completableFuture.complete(null);
        }
    }
}
