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
package cn.starboot.http.server.impl;

import cn.starboot.http.common.enums.*;
import cn.starboot.http.common.exception.HttpException;
import cn.starboot.http.common.logging.Logger;
import cn.starboot.http.common.logging.LoggerFactory;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.HttpRequest;
import cn.starboot.http.server.HttpServerConfiguration;
import cn.starboot.http.server.HttpServerHandler;
import cn.starboot.http.server.WebSocketHandler;
import cn.starboot.socket.StateMachineEnum;
import cn.starboot.socket.core.ChannelContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpMessageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMessageProcessor.class);
    private static final int MAX_LENGTH = 255 * 1024;
    private HttpServerConfiguration configuration;

    public void process0(ChannelContext context, HttpRequestPacket HTTPRequestPacket) {
        Object aioAttachment = context.getAttachment();
        RequestAttachment attachment = (aioAttachment instanceof RequestAttachment) ? (RequestAttachment) aioAttachment : null;
        AbstractRequest abstractRequest = HTTPRequestPacket.newAbstractRequest();
        AbstractResponse response = abstractRequest.getResponse();
        try {
            switch (HTTPRequestPacket.getDecodePartEnum()) {
                case HEADER_FINISH:
                    doHttpHeader(HTTPRequestPacket);
                    if (response.isClosed()) {
                        break;
                    }
                case BODY:
                    onHttpBody(HTTPRequestPacket, context.getReadBuffer().buffer(), attachment);
                    if (response.isClosed() || HTTPRequestPacket.getDecodePartEnum() != DecodePartEnum.FINISH) {
                        break;
                    }
                case FINISH: {
                    //消息处理
                    switch (HTTPRequestPacket.getRequestType()) {
                        case WEBSOCKET:
                            handleWebSocketRequest(context, HTTPRequestPacket);
                            break;
                        case HTTP:
                            handleHttpRequest(context, HTTPRequestPacket);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleWebSocketRequest(ChannelContext context, HttpRequestPacket HTTPRequestPacket) throws IOException {
        AbstractRequest abstractRequest = HTTPRequestPacket.newAbstractRequest();
        CompletableFuture<Object> future = new CompletableFuture<>();
        assert abstractRequest != null;
        HTTPRequestPacket.getServerHandler().handle(abstractRequest, abstractRequest.getResponse(), future);
        if (future.isDone()) {
            finishResponse(abstractRequest);
        } else {
            Thread thread = Thread.currentThread();
            future.thenRun(() -> {
                try {
                    finishResponse(abstractRequest);
                    if (thread != Thread.currentThread()) {
                        // 处理出现异常了，直接关掉就行
//                        context.getWriteBuffer().flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    abstractRequest.getResponse().close();
                } finally {
                    context.signalRead(false);
                }
            });
        }
    }

    private void handleHttpRequest(ChannelContext context, HttpRequestPacket HTTPRequestPacket) throws IOException {
        AbstractRequest abstractRequest = HTTPRequestPacket.newAbstractRequest();
        assert abstractRequest != null;
        AbstractResponse response = abstractRequest.getResponse();
        CompletableFuture<Object> future = new CompletableFuture<>();
        boolean keepAlive = true;
        // http/1.0兼容长连接。
        if (HttpProtocolEnum.HTTP_10.getProtocol().equals(abstractRequest.getProtocol())) {
            keepAlive = HeaderValueEnum.KEEPALIVE.getName().equalsIgnoreCase(abstractRequest.getHeader(HeaderNameEnum.CONNECTION.getName()));
            if (keepAlive) {
                response.setHeader(HeaderNameEnum.CONNECTION.getName(), HeaderValueEnum.KEEPALIVE.getName());
            }
        }
        try {
            HTTPRequestPacket.getServerHandler().handle(abstractRequest, response, future);
            finishHttpHandle(context, abstractRequest, keepAlive, future);
        } catch (HttpException e) {
            e.printStackTrace();
            response.setHttpStatus(HttpStatus.valueOf(e.getHttpCode()));
            response.getOutputStream().write(e.getDesc().getBytes());
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.getOutputStream().write(e.fillInStackTrace().toString().getBytes());
            response.close();
        }
    }

    private void finishHttpHandle(ChannelContext context, AbstractRequest abstractRequest, boolean keepAlive, CompletableFuture<Object> future) throws IOException {
        if (future.isDone()) {
            if (keepConnection(abstractRequest, keepAlive)) {
                finishResponse(abstractRequest);
            }
        } else {
//            session.awaitRead();
            Thread thread = Thread.currentThread();
            AbstractResponse response = abstractRequest.getResponse();
            future.thenRun(() -> {
                try {
                    if (keepConnection(abstractRequest, keepAlive)) {
                        finishResponse(abstractRequest);
                        if (thread != Thread.currentThread()) {
                            // 关闭
//                            context.getWriteBuffer().flush();
                        }
                    }
                } catch (HttpException e) {
                    e.printStackTrace();

                    response.setHttpStatus(HttpStatus.valueOf(e.getHttpCode()));
                    try {
                        response.write(e.getDesc().getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    response.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    try {
                        response.getOutputStream().write(e.fillInStackTrace().toString().getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    response.close();
                } finally {
                    context.signalRead(false);
                }
            });
        }
    }

    private boolean keepConnection(AbstractRequest request, boolean keepAlive) {
        //非keepAlive或者 body部分未读取完毕,释放连接资源
        if (!keepAlive || !HttpMethodEnum.GET.getMethod().equals(request.getMethod()) && request.getContentLength() > 0) {
            request.getResponse().close();
            return false;
        }
        return true;
    }


    private void finishResponse(AbstractRequest abstractRequest) throws IOException {
        AbstractResponse response = abstractRequest.getResponse();
        //关闭本次请求的输出流
        if (!response.getOutputStream().isClosed()) {
            response.getOutputStream().close();
        }
        abstractRequest.reset();
    }


    private void onHttpBody(HttpRequestPacket HTTPRequestPacket, ByteBuffer readBuffer, RequestAttachment attachment) {
        if (HTTPRequestPacket.getServerHandler().onBodyStream(readBuffer, HTTPRequestPacket)) {
            HTTPRequestPacket.setDecodePartEnum(DecodePartEnum.FINISH);
            if (HTTPRequestPacket.getRequestType() == HttpTypeEnum.HTTP) {
                attachment.setDecoder(null);
            }
        } else if (readBuffer.hasRemaining()) {
            //半包,继续读数据
            attachment.setDecoder(HttpRequestHandler.BODY_CONTINUE_DECODER);
        }
    }

    private void doHttpHeader(HttpRequestPacket HTTPRequestPacket) throws IOException {
        methodCheck(HTTPRequestPacket);
        uriCheck(HTTPRequestPacket);
        HTTPRequestPacket.getServerHandler().onHeaderComplete(HTTPRequestPacket);
        HTTPRequestPacket.setDecodePartEnum(DecodePartEnum.BODY);
    }

    public void stateEvent0(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        switch (stateMachineEnum) {
            case NEW_CHANNEL:
                RequestAttachment attachment = new RequestAttachment(new HttpRequestPacket(configuration, channelContext));
                channelContext.setAttachment(attachment);
                break;
            case PROCESS_EXCEPTION:
                LOGGER.error("process exception", throwable);
                channelContext.close(true);
                break;
            case CHANNEL_CLOSED:
                Object aioAttachment = channelContext.getAttachment();
                RequestAttachment att = (aioAttachment instanceof RequestAttachment) ? (RequestAttachment) aioAttachment : null;
                if (att != null && att.getHTTPRequestPacket().getServerHandler() != null) {
                    att.getHTTPRequestPacket().getServerHandler().onClose(att.getHTTPRequestPacket());
                }
                break;
            case DECODE_EXCEPTION:
                throwable.printStackTrace();
                break;
        }
    }

    public void httpServerHandler(HttpServerHandler httpServerHandler) {
        this.configuration.setHttpServerHandler(Objects.requireNonNull(httpServerHandler));
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.configuration.setWebSocketHandler(Objects.requireNonNull(webSocketHandler));
    }

    /**
     * RFC2616 5.1.1 方法标记指明了在被 Request-URI 指定的资源上执行的方法。
     * 这种方法是大小写敏感的。 资源所允许的方法由 Allow 头域指定(14.7 节)。
     * 响应的返回码总是通知客户某个方法对当前资源是否是被允许的，因为被允许的方法能被动态的改变。
     * 如果服务器能理解某方法但此方法对请求资源不被允许的，
     * 那么源服务器应该返回 405 状态码(方法不允许);
     * 如果源服务器不能识别或没有实现某个方法，那么服务器应返回 501 状态码(没有实现)。
     * 方法 GET 和 HEAD 必须被所有一般的服务器支持。 所有其它的方法是可选的;
     * 然而，如果上面的方法都被实现， 这些方法遵循的语意必须和第 9 章指定的相同
     */
    private void methodCheck(HttpRequest request) {
        if (request.getMethod() == null) {
            throw new HttpException(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    /**
     * 1、客户端和服务器都必须支持 Host 请求头域。
     * 2、发送 HTTP/1.1 请求的客户端必须发送 Host 头域。
     * 3、如果 HTTP/1.1 请求不包括 Host 请求头域，服务器必须报告错误 400(Bad Request)。 --服务器必须接受绝对 URIs(absolute URIs)。
     */
    private void hostCheck(HttpRequestPacket HTTPRequestPacket) {
        if (HTTPRequestPacket.getHost() == null) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * RFC2616 3.2.1
     * HTTP 协议不对 URI 的长度作事先的限制，服务器必须能够处理任何他们提供资源的 URI，并 且应该能够处理无限长度的 URIs，这种无效长度的 URL 可能会在客户端以基于 GET 方式的 请求时产生。如果服务器不能处理太长的 URI 的时候，服务器应该返回 414 状态码(此状态码 代表 Request-URI 太长)。
     * 注:服务器在依赖大于 255 字节的 URI 时应谨慎，因为一些旧的客户或代理实现可能不支持这 些长度。
     */
    private void uriCheck(HttpRequestPacket HTTPRequestPacket) {
        String originalUri = HTTPRequestPacket.getUri();
        if (StringUtils.length(originalUri) > MAX_LENGTH) {
            throw new HttpException(HttpStatus.URI_TOO_LONG);
        }
        /**
         *http_URL = "http:" "//" host [ ":" port ] [ abs_path [ "?" query ]]
         *1. 如果 Request-URI 是绝对地址(absoluteURI)，那么主机(host)是 Request-URI 的 一部分。任何出现在请求里 Host 头域的值应当被忽略。
         *2. 假如 Request-URI 不是绝对地址(absoluteURI)，并且请求包括一个 Host 头域，则主 机(host)由该 Host 头域的值决定.
         *3. 假如由规则1或规则2定义的主机(host)对服务器来说是一个无效的主机(host)， 则应当以一个 400(坏请求)错误消息返回。
         */
        if (originalUri.charAt(0) == '/') {
            HTTPRequestPacket.setRequestURI(originalUri);
            return;
        }
        int schemeIndex = originalUri.indexOf("://");
        if (schemeIndex > 0) {//绝对路径
            int uriIndex = originalUri.indexOf('/', schemeIndex + 3);
            if (uriIndex == StringUtils.INDEX_NOT_FOUND) {
                HTTPRequestPacket.setRequestURI("/");
            } else {
                HTTPRequestPacket.setRequestURI(StringUtils.substring(originalUri, uriIndex));
            }
            HTTPRequestPacket.setScheme(StringUtils.substring(originalUri, 0, schemeIndex));
        } else {
            HTTPRequestPacket.setRequestURI(originalUri);
        }
    }

    public void setConfiguration(HttpServerConfiguration configuration) {
        this.configuration = configuration;
    }
}
