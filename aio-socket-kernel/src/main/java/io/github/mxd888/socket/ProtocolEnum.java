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
package io.github.mxd888.socket;

/**
 * 协议枚举类
 */
public enum ProtocolEnum {

    /**
     * HTTP 协议
     */
    HTTP(1000, "http protocol"),

    /**
     * bytes 协议
     */
    BYTES(1001, "bytes protocol"),

    /**
     * string 协议
     */
    STRING(1002, "string protocol"),

    /**
     * base64 协议
     */
    BASE64(1003, "bases64 protocol"),

    /**
     * Protobuf 协议
     */
    PROTOBUF(1004, "protobuf protocol"),

    // ---------------------以下是留给用户的私有化TCP协议枚举类，共五个---------------------

    PRIVATE_TCP(2000, "private TCP protocol"),

    PRIVATE_TCP_1(2001, "private TCP protocol"),

    PRIVATE_TCP_2(2002, "private TCP protocol"),

    PRIVATE_TCP_3(2003, "private TCP protocol"),

    PRIVATE_TCP_4(2004, "private TCP protocol"),

    // ---------------------以下是留给用户的私有化UDP协议枚举类，共五个---------------------

    PRIVATE_UDP(3000, "private TCP protocol"),

    PRIVATE_UDP_1(3001, "private TCP protocol"),

    PRIVATE_UDP_2(3002, "private TCP protocol"),

    PRIVATE_UDP_3(3003, "private TCP protocol"),

    PRIVATE_UDP_4(3004, "private TCP protocol")
    ;

    private final int code;

    private final String msg;

    ProtocolEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getString() {
        return msg;
    }

}
