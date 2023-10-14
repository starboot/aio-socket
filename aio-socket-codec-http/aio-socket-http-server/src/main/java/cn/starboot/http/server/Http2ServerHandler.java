/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server;

import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HeaderValueEnum;
import cn.starboot.http.common.enums.HttpMethodEnum;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.exception.HttpException;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.common.utils.FixedLengthFrameDecoder;
import cn.starboot.http.common.utils.SmartDecoder;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.http.server.impl.WebSocketResponseImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Http消息处理器
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class Http2ServerHandler implements ServerHandler<HttpRequest, HttpResponse> {
    private final Map<HttpRequestPacket, SmartDecoder> bodyDecoderMap = new ConcurrentHashMap<>();

    @Override
    public void onHeaderComplete(HttpRequestPacket HTTPRequestPacket) throws IOException {
        String htt2Settings= HTTPRequestPacket.getHeader(HeaderNameEnum.HTTP2_SETTINGS.getName());
        WebSocketResponseImpl response = HTTPRequestPacket.newWebsocketRequest().getResponse();
        response.setHttpStatus(HttpStatus.SWITCHING_PROTOCOLS);
        response.setHeader(HeaderNameEnum.UPGRADE.getName(), HeaderValueEnum.H2C.getName());
        response.setHeader(HeaderNameEnum.CONNECTION.getName(), HeaderValueEnum.UPGRADE.getName());
        response.flush();
//        OutputStream outputStream = response.getOutputStream();
//        outputStream.flush();
//        response.write(null);
    }

    @Override
    public boolean onBodyStream(ByteBuffer buffer, HttpRequestPacket HTTPRequestPacket) {
        if (HttpMethodEnum.GET.getMethod().equals(HTTPRequestPacket.getMethod())) {
            return true;
        }
        //Post请求
        if (HttpMethodEnum.POST.getMethod().equals(HTTPRequestPacket.getMethod())
                && StringUtils.startsWith(HTTPRequestPacket.getContentType(), HeaderValueEnum.X_WWW_FORM_URLENCODED.getName())) {
            int postLength = HTTPRequestPacket.getContentLength();
            if (postLength > Constant.maxPostSize) {
                throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
            } else if (postLength < 0) {
                throw new HttpException(HttpStatus.LENGTH_REQUIRED);
            }
            SmartDecoder smartDecoder = bodyDecoderMap.computeIfAbsent(HTTPRequestPacket, req -> new FixedLengthFrameDecoder(req.getContentLength()));

            if (smartDecoder.decode(buffer)) {
                bodyDecoderMap.remove(HTTPRequestPacket);
                HTTPRequestPacket.setFormUrlencoded(new String(smartDecoder.getBuffer().array()));
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * 若子类重写 onClose 则必须调用 super.onClose();释放内存
     */
    @Override
    public void onClose(HttpRequestPacket HTTPRequestPacket) {
        bodyDecoderMap.remove(HTTPRequestPacket);
    }

}
