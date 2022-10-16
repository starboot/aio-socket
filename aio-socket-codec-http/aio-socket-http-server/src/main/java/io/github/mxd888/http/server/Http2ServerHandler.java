package io.github.mxd888.http.server;

import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.enums.HeaderValueEnum;
import io.github.mxd888.http.common.enums.HttpMethodEnum;
import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.exception.HttpException;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.FixedLengthFrameDecoder;
import io.github.mxd888.http.common.utils.SmartDecoder;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.http.server.impl.WebSocketResponseImpl;

import java.io.IOException;
import java.io.OutputStream;
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
    private final Map<Request, SmartDecoder> bodyDecoderMap = new ConcurrentHashMap<>();

    @Override
    public void onHeaderComplete(Request request) throws IOException {
        String htt2Settings=request.getHeader(HeaderNameEnum.HTTP2_SETTINGS.getName());
        WebSocketResponseImpl response = request.newWebsocketRequest().getResponse();
        response.setHttpStatus(HttpStatus.SWITCHING_PROTOCOLS);
        response.setHeader(HeaderNameEnum.UPGRADE.getName(), HeaderValueEnum.H2C.getName());
        response.setHeader(HeaderNameEnum.CONNECTION.getName(), HeaderValueEnum.UPGRADE.getName());
//        OutputStream outputStream = response.getOutputStream();
//        outputStream.flush();
        response.write(null);
    }

    @Override
    public boolean onBodyStream(ByteBuffer buffer, Request request) {
        if (HttpMethodEnum.GET.getMethod().equals(request.getMethod())) {
            return true;
        }
        //Post请求
        if (HttpMethodEnum.POST.getMethod().equals(request.getMethod())
                && StringUtils.startsWith(request.getContentType(), HeaderValueEnum.X_WWW_FORM_URLENCODED.getName())) {
            int postLength = request.getContentLength();
            if (postLength > Constant.maxPostSize) {
                throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
            } else if (postLength < 0) {
                throw new HttpException(HttpStatus.LENGTH_REQUIRED);
            }
            SmartDecoder smartDecoder = bodyDecoderMap.computeIfAbsent(request, req -> new FixedLengthFrameDecoder(req.getContentLength()));

            if (smartDecoder.decode(buffer)) {
                bodyDecoderMap.remove(request);
                request.setFormUrlencoded(new String(smartDecoder.getBuffer().array()));
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
    public void onClose(Request request) {
        bodyDecoderMap.remove(request);
    }

}
