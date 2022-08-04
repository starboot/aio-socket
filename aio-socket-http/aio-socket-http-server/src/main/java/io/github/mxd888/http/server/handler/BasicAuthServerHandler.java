
package io.github.mxd888.http.server.handler;



import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.HttpRequest;
import io.github.mxd888.http.server.HttpResponse;
import io.github.mxd888.http.server.HttpServerHandler;
import io.github.mxd888.http.server.impl.Request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * @author 三刀（zhengjunweimail@163.com）
 * @version V1.0 , 2021/2/23
 */
public final class BasicAuthServerHandler extends HttpServerHandler {
    private final HttpServerHandler httpServerHandler;
    private final String basic;

    public BasicAuthServerHandler(String username, String password, HttpServerHandler httpServerHandler) {
        this.httpServerHandler = httpServerHandler;
        basic = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @Override
    public void onHeaderComplete(Request request) throws IOException {
        String clientBasic = request.getHeader(HeaderNameEnum.AUTHORIZATION.getName());
        if (StringUtils.equals(clientBasic, this.basic)) {
            httpServerHandler.onHeaderComplete(request);
        } else {
            HttpResponse response = request.newHttpRequest().getResponse();
            response.setHeader(HeaderNameEnum.WWW_AUTHENTICATE.getName(), "Basic realm=\"smart-http\"");
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.close();
        }
    }

    @Override
    public boolean onBodyStream(ByteBuffer buffer, Request request) {
        return httpServerHandler.onBodyStream(buffer, request);
    }

    @Override
    public void onClose(Request request) {
        httpServerHandler.onClose(request);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        httpServerHandler.handle(request, response);
    }

}
