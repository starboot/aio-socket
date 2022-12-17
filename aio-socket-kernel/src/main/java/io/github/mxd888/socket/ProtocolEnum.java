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
	 * http2 协议
	 */
	HTTP2(1001, "http2 protocol"),

	/**
	 * websocket 协议
	 */
	WEBSOCKET(1002, "websocket protocol"),

    /**
     * bytes 协议
     */
    BYTES(1003, "bytes protocol"),

    /**
     * string 协议
     */
    STRING(1004, "string protocol"),

    /**
     * base64 协议
     */
    BASE64(1005, "bases64 protocol"),

    /**
     * Protobuf 协议
     */
    PROTOBUF(1006, "protobuf protocol"),

	/**
	 * mqtt v3.1 协议
	 */
	MQTT_v3_1(1007, "mqtt v3.1 protocol"),

	/**
	 * mqtt v3.1.1 协议
	 */
	MQTT_v3_1_1(1008, "mqtt v3.1.1 protocol"),

	/**
	 * mqtt v5.0 协议
	 */
	MQTT_v5_0(1009, "mqtt v5.0 protocol"),

    // ---------------------以下是留给用户的私有化TCP协议枚举类，共五个---------------------

    PRIVATE_TCP(2000, "private TCP(2000) protocol"),

    PRIVATE_TCP_1(2001, "private TCP(2001) protocol"),

    PRIVATE_TCP_2(2002, "private TCP(2002) protocol"),

    PRIVATE_TCP_3(2003, "private TCP(2003) protocol"),

    PRIVATE_TCP_4(2004, "private TCP(2004) protocol"),

    // ---------------------以下是留给用户的私有化UDP协议枚举类，共五个---------------------

    PRIVATE_UDP(3000, "private UDP(3000) protocol"),

    PRIVATE_UDP_1(3001, "private UDP(3001) protocol"),

    PRIVATE_UDP_2(3002, "private UDP(3002) protocol"),

    PRIVATE_UDP_3(3003, "private UDP(3003) protocol"),

    PRIVATE_UDP_4(3004, "private UDP(3004) protocol")
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
