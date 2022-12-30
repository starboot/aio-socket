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
package cn.starboot.http.server;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface WebSocketResponse {
    /**
     * 发送文本响应
     *
     * @param text
     */
    void sendTextMessage(String text);

    /**
     * 发送二进制响应
     *
     * @param bytes
     */
    void sendBinaryMessage(byte[] bytes);
    /**
     * 发送二进制响应
     *
     * @param bytes
     */
    void sendBinaryMessage(byte[] bytes,int offset, int length);

    /**
     * 关闭ws通道
     */
    void close();

    /**
     * 输出数据
     */
    void flush();
}
