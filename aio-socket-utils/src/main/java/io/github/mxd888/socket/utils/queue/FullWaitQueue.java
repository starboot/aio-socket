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
package io.github.mxd888.socket.utils.queue;

/**
 * 改进的线性安全队列
 *
 * @param <T> 单元类型
 */
public interface FullWaitQueue<T> {

    /**
     * 入队
     * @param t 入队单元
     * @return 入队状态
     */
    boolean offer(T t);

    /**
     * 出队
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    T poll();

    void clear();

    int size();

    boolean isEmpty();
}
