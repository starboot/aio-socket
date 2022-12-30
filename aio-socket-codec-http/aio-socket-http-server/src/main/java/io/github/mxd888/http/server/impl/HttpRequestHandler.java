package io.github.mxd888.http.server.impl;

import cn.starboot.socket.exception.AioEncoderException;
import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.decode.Decoder;
import io.github.mxd888.http.server.decode.HttpMethodDecoder;
import cn.starboot.socket.Packet;
import cn.starboot.socket.ProtocolEnum;
import cn.starboot.socket.StateMachineEnum;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.intf.AioHandler;
import cn.starboot.socket.utils.pool.memory.MemoryUnit;
import io.github.mxd888.http.server.encode.HttpResponseEncoder;

import java.util.Arrays;
import java.util.function.BiConsumer;

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

    private final HttpResponseEncoder encoder;

    public HttpRequestHandler(HttpServerConfiguration configuration, HttpMessageProcessor processor) {
        this.httpMethodDecoder = new HttpMethodDecoder(configuration);
        this.processor = processor;
        this.encoder = new HttpResponseEncoder();
    }

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof HttpRequestPacket) {
            HttpRequestPacket packet1 = (HttpRequestPacket) packet;
            System.out.println(packet1.getProtocol() + "--" + packet1.getMethod() + "--" + packet1.getContentType() +
                    "--" + packet1.getHeaderNames() + "--"  + packet1.getHeader("Upgrade"));
            packet1.getParameters().forEach(new BiConsumer<String, String[]>() {
                @Override
                public void accept(String s, String[] strings) {
                    System.out.println(s + "---" + Arrays.toString(strings));
                }
            });
            processor.process0(channelContext, (HttpRequestPacket) packet);
        }else {
            System.out.println("在io.github.mxd888.http.server.impl.HttpRequestHandler的handle方法里出现http处理错误");
        }
        return null;
    }

    @Override
    public Packet decode(final MemoryUnit readBuffer, ChannelContext channelContext) {
        if (readBuffer.buffer().remaining() == 0) {
            return null;
        }
        Object aioAttachment = channelContext.getAttachment();
        RequestAttachment attachment = (aioAttachment instanceof RequestAttachment) ? (RequestAttachment) aioAttachment : null;
        HttpRequestPacket HTTPRequestPacket = attachment.getHTTPRequestPacket();
        Decoder decodeChain = attachment.getDecoder();
        if (decodeChain == null) {
            decodeChain = httpMethodDecoder;
        }
        // 数据还未就绪，继续读
        if (decodeChain == BODY_CONTINUE_DECODER) {
            attachment.setDecoder(BODY_READY_DECODER);
            return null;
        } else if (decodeChain == BODY_READY_DECODER) {
            return HTTPRequestPacket;
        }

        decodeChain = decodeChain.decode(readBuffer.buffer(), channelContext, HTTPRequestPacket);
        attachment.setDecoder(decodeChain);
        if (decodeChain == BODY_READY_DECODER) {
            return HTTPRequestPacket;
        }
        if (readBuffer.buffer().remaining() == readBuffer.buffer().capacity()) {
            throw new RuntimeException("buffer is too small when decode " + decodeChain.getClass().getName() + " ," + HTTPRequestPacket);
        }
        return null;
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) throws AioEncoderException {
		if (packet instanceof HttpResponsePacket) {
			HttpResponsePacket httpResponsePacket = (HttpResponsePacket) packet;
			encoder.encode(httpResponsePacket, channelContext);
		}

    }

    @Override
    public void stateEvent(ChannelContext channelContext, StateMachineEnum stateMachineEnum, Throwable throwable) {
        processor.stateEvent0(channelContext, stateMachineEnum, throwable);
    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.HTTP;
    }
}

