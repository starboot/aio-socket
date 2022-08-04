package io.github.mxd888.http.server;

import io.github.mxd888.http.common.enums.HeaderValueEnum;
import io.github.mxd888.http.common.enums.HttpMethodEnum;
import io.github.mxd888.http.common.enums.HttpStatus;
import io.github.mxd888.http.common.exception.HttpException;
import io.github.mxd888.http.common.utils.FixedLengthFrameDecoder;
import io.github.mxd888.http.common.utils.SmartDecoder;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.http.server.impl.RequestAttachment;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class HttpServerHandler implements ServerHandler<HttpRequest, HttpResponse> {

    @Override
    public boolean onBodyStream(ByteBuffer buffer, Request request) {
        if (HttpMethodEnum.GET.getMethod().equals(request.getMethod())) {
            return true;
        }
        //Post请求
        if (HttpMethodEnum.POST.getMethod().equals(request.getMethod())
                && StringUtils.startsWith(request.getContentType(), HeaderValueEnum.X_WWW_FORM_URLENCODED.getName())) {
            int postLength = request.getContentLength();
            if (postLength > request.getConfiguration().getMaxFormContentSize()) {
                throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
            } else if (postLength < 0) {
                throw new HttpException(HttpStatus.LENGTH_REQUIRED);
            }
            Object aioAttachment = request.getAioChannelContext().getAttachment();
            RequestAttachment attachment = (aioAttachment instanceof RequestAttachment) ? (RequestAttachment) aioAttachment : null;
            SmartDecoder smartDecoder = attachment.getBodyDecoder();
            if (smartDecoder == null) {
                smartDecoder = new FixedLengthFrameDecoder(request.getContentLength());
                attachment.setBodyDecoder(smartDecoder);
            }

            if (smartDecoder.decode(buffer)) {
                request.setFormUrlencoded(new String(smartDecoder.getBuffer().array()));
                attachment.setBodyDecoder(null);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
