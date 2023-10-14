/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server.impl;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpResponseImpl extends AbstractResponse {

	/* uid */
	private static final long serialVersionUID = -1563035926880261214L;

	public HttpResponseImpl(HttpRequestImpl httpRequest, HttpRequestPacket HTTPRequestPacket) {
        init(httpRequest, new HttpOutputStream(httpRequest, this, HTTPRequestPacket));
    }
}
