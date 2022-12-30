/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
