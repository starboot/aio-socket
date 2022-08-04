/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpServerHandle.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

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
 * Http消息处理器
 *
 * @author 三刀
 * @version V1.0 , 2018/2/6
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
            RequestAttachment attachment = request.getAioChannelContext().getAttachment();
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
