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
package cn.starboot.http.server.handler;

import cn.starboot.http.server.WebSocketHandler;
import cn.starboot.http.server.WebSocketRequest;
import cn.starboot.http.server.WebSocketResponse;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.http.server.impl.WebSocketRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class WebSocketDefaultHandler extends WebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketDefaultHandler.class);

    @Override
    public void onHeaderComplete(HttpRequestPacket HTTPRequestPacket) throws IOException {
        super.onHeaderComplete(HTTPRequestPacket);
        WebSocketRequestImpl webSocketRequest = HTTPRequestPacket.newWebsocketRequest();
        onHandShake(webSocketRequest, webSocketRequest.getResponse());
    }

    @Override
    public final void handle(WebSocketRequest request, WebSocketResponse response) throws IOException {
        try {
            switch (request.getFrameOpcode()) {
                case WebSocketRequestImpl.OPCODE_TEXT:
                    handleTextMessage(request, response, new String(request.getPayload(), StandardCharsets.UTF_8));
                    break;
                case WebSocketRequestImpl.OPCODE_BINARY:
                    handleBinaryMessage(request, response, request.getPayload());
                    break;
                case WebSocketRequestImpl.OPCODE_CLOSE:
                    try {
                        onClose(request, response);
                    } finally {
                        response.close();
                    }
                    break;
                case WebSocketRequestImpl.OPCODE_PING:
//                            LOGGER.warn("unSupport ping now");
                    break;
                case WebSocketRequestImpl.OPCODE_PONG:
//                            LOGGER.warn("unSupport pong now");
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (Throwable throwable) {
            onError(request,throwable);
            throw throwable;
        }
    }

    /**
     * 握手成功
     *
     * @param request
     * @param response
     */
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("handShake success");
    }

    /**
     * 连接关闭
     *
     * @param request
     * @param response
     */
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("close connection");
    }

    /**
     * 处理字符串请求消息
     *
     * @param request
     * @param response
     * @param data
     */
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        System.out.println(data);
    }

    /**
     * 处理二进制请求消息
     *
     * @param request
     * @param response
     * @param data
     */
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        System.out.println(data);
    }

    /**
     * 连接异常
     *
     * @param request
     * @param throwable
     */
    public void onError(WebSocketRequest request,Throwable throwable) {
        throwable.printStackTrace();
    }
}
