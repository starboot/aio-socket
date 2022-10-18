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
package io.github.mxd888.socket.intf;

/**
 * 抽象处理器，使用责任链模式进行解码
 */
public abstract class AioHandler implements Handler, IProtocol{

    /**
     * 当期那处理器的后面一个处理器指针，无处理器则为空指针
     */
    private AioHandler nextHandler;

    /**
     * 获取后方处理器
     *
     * @return 抽象处理器
     */
    public AioHandler Next() {
        return this.nextHandler;
    }

    /**
     * 设置下一个处理器
     *
     * @param handler 抽象处理器
     */
    public void setNext(AioHandler handler) {
        this.nextHandler = handler;
    }
}
