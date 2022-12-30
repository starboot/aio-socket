package cn.starboot.http.server;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface WebSocketRequest {

    public int getFrameOpcode();

    public byte[] getPayload();

    String getRequestURI();

    String getQueryString();

    Map<String, String[]> getParameters();

    InetSocketAddress getRemoteAddress();

    /**
     * 获取套接字绑定的本地地址。
     *
     * @return
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取附件对象
     *
     * @param <A> 附件对象类型
     * @return 附件
     */
    <A> A getAttachment();

    /**
     * 存放附件，支持任意类型
     *
     * @param <A>        附件对象类型
     * @param attachment 附件对象
     */
    <A> void setAttachment(A attachment);
}
