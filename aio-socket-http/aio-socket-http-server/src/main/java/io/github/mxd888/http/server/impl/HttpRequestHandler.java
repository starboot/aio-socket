package io.github.mxd888.http.server.impl;

import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.decode.Decoder;
import io.github.mxd888.http.server.decode.HttpMethodDecoder;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.TCPChannelContext;
import io.github.mxd888.socket.intf.AioHandler;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
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
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof Request) {
            processor.process0(channelContext, (Request) packet);
        }else {
            System.out.println("在io.github.mxd888.http.server.impl.HttpRequestHandler的handle方法里出现http处理错误");
        }
        return null;
    }

    @Override
    public Packet decode(final VirtualBuffer readBuffer, ChannelContext channelContext) {
        if (readBuffer.buffer().remaining() == 0) {
            return null;
        }
        Object aioAttachment = channelContext.getAttachment();
        RequestAttachment attachment = (aioAttachment instanceof RequestAttachment) ? (RequestAttachment) aioAttachment : null;
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
    public void encode(Packet packet, ChannelContext channelContext) {
    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        processor.stateEvent0(channelContext, stateMachineEnum, throwable);
    }
}

