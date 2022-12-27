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
package cn.starboot.socket.plugins.ssl;

/**
 * 配置引擎请求客户端验证。此选项只对服务器模式的引擎有用
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public enum ClientAuth {
    /**
     * 不需要客户端验证
     */
    NONE,
    /**
     * 请求的客户端验证
     * 如果设置了此选项并且客户端选择不提供其自身的验证信息，则协商将会继续
     */
    OPTIONAL,
    /**
     * 必须的客户端验证
     * 如果设置了此选项并且客户端选择不提供其自身的验证信息，则协商将会停止且引擎将开始它的关闭过程
     */
    REQUIRE
}
