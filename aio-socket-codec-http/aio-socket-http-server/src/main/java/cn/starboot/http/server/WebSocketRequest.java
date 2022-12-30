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

import java.net.InetSocketAddress;
import java.util.Map;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface WebSocketRequest {

    public int getFrameOpcode();

    public byte[] getPayload();

    String getRequestURI();

    String getQueryString();

    Map<String, String[]> getParameters();

    InetSocketAddress getRemoteAddress();

    /**
     * 获取套接字绑定的本地地址。
     *
     * @return
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取附件对象
     *
     * @param <A> 附件对象类型
     * @return 附件
     */
    <A> A getAttachment();

    /**
     * 存放附件，支持任意类型
     *
     * @param <A>        附件对象类型
     * @param attachment 附件对象
     */
    <A> void setAttachment(A attachment);
}
