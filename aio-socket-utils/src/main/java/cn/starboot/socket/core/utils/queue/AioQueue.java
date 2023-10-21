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
package cn.starboot.socket.core.utils.queue;

/**
 * 改进的线性安全队列
 *
 * @param <T> 单元类型
 */
public interface AioQueue<T> {

    /**
     * 入队
     *
     * @param t 入队单元
     * @return 入队状态
     */
    boolean offer(T t);

    /**
     * 出队但不删除头元素
     *
     * @return 头元素
     */
    T peek();

    /**
     * 出队删除头元素
     *
     * @return 头元素
     */
    T poll();

    /**
     * 清空队列
     */
    void clear();

    /**
     * 队列大小
     *
     * @return 队列大小
     */
    int size();

    /**
     * 队列是否为空
     *
     * @return 布尔
     */
    boolean isEmpty();

    /**
     * 是否满队列了
     *
     * @return 布尔
     */
    boolean isFull();
}
