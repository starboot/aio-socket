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

import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.server.WebSocketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class WebSocketResponseImpl extends AbstractResponse implements WebSocketResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketResponseImpl.class);

    public WebSocketResponseImpl(WebSocketRequestImpl webSocketRequest, HttpRequestPacket HTTPRequestPacket) {
        init(webSocketRequest, new WebSocketOutputStream(webSocketRequest, this, HTTPRequestPacket));
    }

    @Override
    public void sendTextMessage(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try {
            send(WebSocketRequestImpl.OPCODE_TEXT, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes) {
        try {
            send(WebSocketRequestImpl.OPCODE_BINARY, bytes,0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes, int offset, int length) {
      try {
        send(WebSocketRequestImpl.OPCODE_BINARY, bytes, offset, length);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void flush() {
        try {
            getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(byte opCode, byte[] bytes, int offset, int len) throws IOException {
      int maxlength;
        if (len < Constant.WS_PLAY_LOAD_126) {
            maxlength = 2 + len;
        } else if (len < Constant.WS_DEFAULT_MAX_FRAME_SIZE) {
            maxlength = 4 + len;
        } else {
            maxlength = 4 + Constant.WS_DEFAULT_MAX_FRAME_SIZE;
        }
        byte[] writBytes = new byte[maxlength];

        while (offset < len) {
            int length = len - offset;
            if (length > Constant.WS_DEFAULT_MAX_FRAME_SIZE) {
                length = Constant.WS_DEFAULT_MAX_FRAME_SIZE;
            }
            byte firstByte = offset + length < len ? (byte) 0x00 : (byte) 0x80;
            if (offset == 0) {
                firstByte |= opCode;
            } else {
                firstByte |= WebSocketRequestImpl.OPCODE_CONT;
            }
            byte secondByte = length < Constant.WS_PLAY_LOAD_126 ? (byte) length : Constant.WS_PLAY_LOAD_126;
            writBytes[0] = firstByte;
            writBytes[1] = secondByte;
            if (secondByte == Constant.WS_PLAY_LOAD_126) {
                writBytes[2] = (byte) (length >> 8 & 0xff);
                writBytes[3] = (byte) (length & 0xff);
                System.arraycopy(bytes, offset, writBytes, 4, length);
            } else {
                System.arraycopy(bytes, offset, writBytes, 2, length);
            }
            this.write(writBytes, 0, length < Constant.WS_PLAY_LOAD_126 ? 2 + length : 4 + length);
//            this.getOutputStream().write(writBytes, 0, length < Constant.WS_PLAY_LOAD_126 ? 2 + length : 4 + length);
            offset += length;
        }
    }
}
