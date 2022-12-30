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
public enum HttpMethodEnum {

    OPTIONS("OPTIONS"),

    GET("GET"),

    HEAD("HEAD"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    TRACE("TRACE"),

    CONNECT("CONNECT")
	;

    private final String method;

    HttpMethodEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
