/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.common.io;

import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.exception.HttpException;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.socket.core.ChannelContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChunkedInputStream extends InputStream {
	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(8);
	private boolean readFlag = true;
	private final ChannelContext channelContext;
	private InputStream inputStream;
	private boolean eof = false;

	public ChunkedInputStream(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public int read() {
		throw new UnsupportedOperationException("unsafe operation");
//        readChunkedLength();
//        if (eof) {
//            return -1;
//        }
//        int b = inputStream.read();
//        if (b == -1) {
//            inputStream.close();
//            inputStream = channelContext.getInputStream();
//            readCrlf();
//            readFlag = true;
//            return read();
//        }
//        return b;
	}

	@Override
	public int read(byte[] data, int off, int len) throws IOException {
		readChunkedLength();
		if (eof) {
			return -1;
		}
		int i = inputStream.read(data, off, len);
		if (i == -1) {
			inputStream.close();
			inputStream = channelContext.getInputStream();
			readCrlf();
			readFlag = true;
			return read(data, off, len);
		}
		return i;
	}

	private void readChunkedLength() throws IOException {
		while (readFlag) {
			inputStream = channelContext.getInputStream();
			int b = inputStream.read();
			if (b == Constant.LF) {
				int length = Integer.parseInt(buffer.toString(), 16);
				buffer.reset();
				if (length == 0) {
					eof = true;
					readCrlf();
					break;
				}
				inputStream.close();
				inputStream = channelContext.getInputStream(length);
				readFlag = false;
			} else if (b != Constant.CR) {
				buffer.write(b);
			}
		}
	}

	private void readCrlf() throws IOException {
		if (inputStream.read() != Constant.CR) {
			throw new HttpException(HttpStatus.BAD_REQUEST);
		}
		if (inputStream.read() != Constant.LF) {
			throw new HttpException(HttpStatus.BAD_REQUEST);
		}
	}
}
