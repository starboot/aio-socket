package io.github.mxd888.http.server.impl;

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
