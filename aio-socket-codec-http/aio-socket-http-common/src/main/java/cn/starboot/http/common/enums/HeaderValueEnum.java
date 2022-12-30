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
package cn.starboot.http.common.enums;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum HeaderValueEnum {

    CHUNKED("chunked"),

    MULTIPART_FORM_DATA("multipart/form-data"),

    X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),

    APPLICATION_JSON("application/json"),

    UPGRADE("Upgrade"),

    WEBSOCKET("websocket"),

    KEEPALIVE("Keep-Alive"),

    keepalive("keep-alive"),

    DEFAULT_CONTENT_TYPE("text/html; charset=utf-8"),

    CONTINUE("100-continue"),

    GZIP("gzip"),

    H2("h2"),

    H2C("h2c")
	;

    private final String name;

    HeaderValueEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
