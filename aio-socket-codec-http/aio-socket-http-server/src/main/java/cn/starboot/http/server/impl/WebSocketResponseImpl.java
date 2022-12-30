package cn.starboot.http.server.impl;

import cn.starboot.http.common.logging.Logger;
import cn.starboot.http.common.logging.LoggerFactory;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.server.WebSocketResponse;

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
        if(LOGGER.isInfoEnabled())
            LOGGER.info("发送字符串消息: " + text);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try {
            send(WebSocketRequestImpl.OPCODE_TEXT, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendBinaryMessage(byte[] bytes) {
        if(LOGGER.isInfoEnabled())
            LOGGER.info("发送二进制消息: " + Arrays.toString(bytes));
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
