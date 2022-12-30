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
package cn.starboot.http.server.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpRequestImpl extends AbstractRequest {
    private final HttpResponseImpl response;
    private InputStream inputStream;

    HttpRequestImpl(HttpRequestPacket HTTPRequestPacket) {
        init(HTTPRequestPacket);
        this.response = new HttpResponseImpl(this, HTTPRequestPacket);
    }

    public final HttpResponseImpl getResponse() {
        return response;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    public void reset() {
        HTTPRequestPacket.reset();
        response.reset();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }
    }



}
