/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.http.server;

import cn.starboot.http.common.enums.HeaderValueEnum;
import cn.starboot.http.common.enums.HttpMethodEnum;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.exception.HttpException;
import cn.starboot.http.common.utils.FixedLengthFrameDecoder;
import cn.starboot.http.common.utils.SmartDecoder;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.impl.HttpRequestPacket;
import cn.starboot.http.server.impl.RequestAttachment;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class HttpServerHandler implements ServerHandler<HttpRequest, HttpResponse> {

    @Override
    public boolean onBodyStream(ByteBuffer buffer, HttpRequestPacket HTTPRequestPacket) {
        if (HttpMethodEnum.GET.getMethod().equals(HTTPRequestPacket.getMethod())) {
            return true;
        }
        //Post请求
        if (HttpMethodEnum.POST.getMethod().equals(HTTPRequestPacket.getMethod())
                && StringUtils.startsWith(HTTPRequestPacket.getContentType(), HeaderValueEnum.X_WWW_FORM_URLENCODED.getName())) {
            int postLength = HTTPRequestPacket.getContentLength();
            if (postLength > HTTPRequestPacket.getConfiguration().getMaxFormContentSize()) {
                throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
            } else if (postLength < 0) {
                throw new HttpException(HttpStatus.LENGTH_REQUIRED);
            }
            Object aioAttachment = HTTPRequestPacket.getAioChannelContext().getAttachment();
            RequestAttachment attachment = (aioAttachment instanceof RequestAttachment) ? (RequestAttachment) aioAttachment : null;
            SmartDecoder smartDecoder = attachment.getBodyDecoder();
            if (smartDecoder == null) {
                smartDecoder = new FixedLengthFrameDecoder(HTTPRequestPacket.getContentLength());
                attachment.setBodyDecoder(smartDecoder);
            }

            if (smartDecoder.decode(buffer)) {
                HTTPRequestPacket.setFormUrlencoded(new String(smartDecoder.getBuffer().array()));
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
