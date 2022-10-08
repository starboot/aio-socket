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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * 改进的线性安全队列
 *
 * @param <T> 单元类型
 */
public class AioFullWaitQueue<T> implements FullWaitQueue<T> {

    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

    private final Semaphore capacity;

    private final Integer total;

    public AioFullWaitQueue(Integer capacity) {
        this.capacity = new Semaphore(capacity);
        this.total = capacity;
    }

    @Override
    public boolean offer(T t) {
        try {
            capacity.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return queue.offer(t);
    }

    @Override
    public T peek() {
        return queue.peek();
    }

    /**
     * 拿的时候不需要判空，直接拿，完事看拿到的是不是空
     * @return t 单元
     */
    @Override
    public T poll() {
        T poll = queue.poll();
        if (poll != null) {
            capacity.release();
        }
        return poll;
    }

    @Override
    public void clear() {
        queue.clear();
    }

    /**
     * 改善后的size()性能大幅提高
     * @return 大小
     */
    @Override
    public int size() {
        return total - capacity.availablePermits();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean isFull() {
        return capacity.availablePermits() == 0;
    }

}
