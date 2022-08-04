/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpRequestHandler.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package io.github.mxd888.http.server.impl;



import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.decode.Decoder;
import io.github.mxd888.http.server.decode.HttpMethodDecoder;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.intf.AioHandler;

import java.nio.ByteBuffer;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/31
 */
public class HttpRequestHandler implements AioHandler {
    public static final Decoder BODY_READY_DECODER = (byteBuffer, aioSession, response) -> null;
    public static final Decoder BODY_CONTINUE_DECODER = (byteBuffer, aioSession, response) -> null;
    /**
     * websocket负载数据读取成功
     */
    private final HttpMethodDecoder httpMethodDecoder;

    private final HttpMessageProcessor processor;

    public HttpRequestHandler(HttpServerConfiguration configuration, HttpMessageProcessor processor) {
        this.httpMethodDecoder = new HttpMethodDecoder(configuration);
        this.processor = processor;
    }

    @Override
    public void handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof Request) {
            processor.process0(channelContext, (Request) packet);
        }else {
            System.out.println("在io.github.mxd888.http.server.impl.HttpRequestHandler的handle方法里出现http处理错误");
        }

    }

    @Override
    public Packet decode(final VirtualBuffer readBuffer, ChannelContext channelContext) {
        if (readBuffer.buffer().remaining() == 0) {
            return null;
        }
        RequestAttachment attachment = channelContext.getAttachment();

        Request request = attachment.getRequest();
        Decoder decodeChain = attachment.getDecoder();
        if (decodeChain == null) {
            decodeChain = httpMethodDecoder;
        }
        // 数据还未就绪，继续读
        if (decodeChain == BODY_CONTINUE_DECODER) {
            attachment.setDecoder(BODY_READY_DECODER);
            return null;
        } else if (decodeChain == BODY_READY_DECODER) {
            return request;
        }

        decodeChain = decodeChain.decode(readBuffer.buffer(), channelContext, request);
        attachment.setDecoder(decodeChain);
        if (decodeChain == BODY_READY_DECODER) {
            return request;
        }
        if (readBuffer.buffer().remaining() == readBuffer.buffer().capacity()) {
            throw new RuntimeException("buffer is too small when decode " + decodeChain.getClass().getName() + " ," + request);
        }
        return null;
    }

    @Override
    public VirtualBuffer encode(Packet packet, ChannelContext channelContext, VirtualBuffer writeBuffer) {
        return null;
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        processor.stateEvent0(channelContext, stateMachineEnum, throwable);
    }
}

