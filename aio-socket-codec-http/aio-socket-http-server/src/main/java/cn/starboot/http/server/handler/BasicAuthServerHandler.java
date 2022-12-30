package cn.starboot.http.server.handler;

import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.HttpRequest;
import cn.starboot.http.server.HttpResponse;
import cn.starboot.http.server.HttpServerHandler;
import cn.starboot.http.server.impl.HttpRequestPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class BasicAuthServerHandler extends HttpServerHandler {
    private final HttpServerHandler httpServerHandler;
    private final String basic;

    public BasicAuthServerHandler(String username, String password, HttpServerHandler httpServerHandler) {
        this.httpServerHandler = httpServerHandler;
        basic = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @Override
    public void onHeaderComplete(HttpRequestPacket HTTPRequestPacket) throws IOException {
        String clientBasic = HTTPRequestPacket.getHeader(HeaderNameEnum.AUTHORIZATION.getName());
        if (StringUtils.equals(clientBasic, this.basic)) {
            httpServerHandler.onHeaderComplete(HTTPRequestPacket);
        } else {
            HttpResponse response = HTTPRequestPacket.newHttpRequest().getResponse();
            response.setHeader(HeaderNameEnum.WWW_AUTHENTICATE.getName(), "Basic realm=\"smart-http\"");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.close();
        }
    }

    @Override
    public boolean onBodyStream(ByteBuffer buffer, HttpRequestPacket HTTPRequestPacket) {
        return httpServerHandler.onBodyStream(buffer, HTTPRequestPacket);
    }

    @Override
    public void onClose(HttpRequestPacket HTTPRequestPacket) {
        httpServerHandler.onClose(HTTPRequestPacket);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        httpServerHandler.handle(request, response);
    }

}
