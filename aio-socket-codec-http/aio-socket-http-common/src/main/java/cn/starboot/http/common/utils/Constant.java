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
package cn.starboot.http.common.utils;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface Constant {
    int WS_DEFAULT_MAX_FRAME_SIZE = (2 << 16) - 1;
    int WS_PLAY_LOAD_126 = 126;
    int WS_PLAY_LOAD_127 = 127;

    /**
     * Post 最大长度
     */
    int maxPostSize = 2 * 1024 * 1024;

    String SCHEMA_HTTP = "http";
    /**
     * Horizontal space
     */
    byte SP = 32;

    /**
     * Carriage return
     */
    byte CR = 13;

    /**
     * Line feed character
     */
    byte LF = 10;

    /**
     * Colon ':'
     */
    byte COLON = 58;


    /**
     * Horizontal space
     */
    char SP_CHAR = (char) SP;

    char COLON_CHAR = COLON;

    byte[] CRLF_BYTES = {Constant.CR, Constant.LF};

    String CRLF = "\r\n";

    byte[] CHUNKED_END_BYTES = "0\r\n\r\n".getBytes(StandardCharsets.US_ASCII);

}
