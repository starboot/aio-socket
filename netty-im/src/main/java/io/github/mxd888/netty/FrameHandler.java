package io.github.mxd888.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * WebSocket 长连接下 文本帧的处理器
 * 实现浏览器发送文本回写
 * 浏览器连接状态监控
 *
 * @author makeDoBetter
 * @version 1.0
 * @date 2021/4/29 16:30
 * @since JDK 1.8
 */
public class FrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //使用msg.text()获得帧中文本
        System.out.println(msg.text());
        //回写，需要封装成TextWebSocketFrame 对象写入到通道中
        ctx.channel().writeAndFlush(new TextWebSocketFrame("【服务端】" + LocalDateTime.now() + msg.text()));
    }

    /**
     * 出现异常的处理 打印报错日志
     *
     * @param ctx   the ctx
     * @param cause the cause
     * @throws Exception the Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        //关闭上下文
        ctx.close();
    }

    /**
     * 监控浏览器上线
     *
     * @param ctx the ctx
     * @throws Exception the Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().id().asShortText() + "连接");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().id().asShortText() + "断开连接");
    }
}