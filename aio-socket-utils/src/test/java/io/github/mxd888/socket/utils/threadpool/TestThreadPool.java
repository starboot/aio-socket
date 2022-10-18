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
package io.github.mxd888.socket.utils.threadpool;

import io.github.mxd888.socket.utils.ThreadUtils;
import io.github.mxd888.socket.utils.pool.thread.AbstractQueueRunnable;
import io.github.mxd888.socket.utils.queue.AioFullWaitQueue;
import io.github.mxd888.socket.utils.queue.AioQueue;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * 线程池测试：
 *      ThreadPoolExecutor: 性能较高，但执行无序
 *      SynThreadPoolExecutor： 先来先执行线程池，性能微差（因为每次执行任务涉及到从队列取任务）
 *
 * 硬件参数：PC(CPU: Intel(R) Core(TM) i7-9700 @ 3.00GHz; Memory: 16GB; 无固态; 机械盘: TOSHIBA DT01ACA200 联想硬盘)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class TestThreadPool {


    public static void main(String[] args) {

//        testThreadPoolExecutor();

        testSynThreadPoolExecutor();

    }

    /**
     * 按序执行
     */
    private static void testSynThreadPoolExecutor() {
        ExecutorService aioExecutor = ThreadUtils.getAioExecutor();
        testAioRunnable testSynRunnable = new testAioRunnable(aioExecutor);
        for (int i = 0; i < 100; i++) {
            testSynRunnable.addTask("我是" + i +"号");
            testSynRunnable.execute();
        }
    }

    /**
     * 异步线程池；执行顺序是随机的，没有先来后到
     */
    private static void testThreadPoolExecutor() {
        ExecutorService groupExecutor = ThreadUtils.getGroupExecutor();

        for (int i = 0; i < 100; i++) {
            groupExecutor.execute(new testRunnable("我是" + i +"号"));
        }
    }

    static class testRunnable implements Runnable {

        private final String s;

        public testRunnable(String s) {
            this.s = s;
        }

        @Override
        public void run() {
            System.out.println(this.s);
        }
    }

    static class testAioRunnable extends AbstractQueueRunnable<String> {

        private AioQueue<String> msgQueue = null;

        protected testAioRunnable(Executor executor) {
            super(executor);
        }

        @Override
        public AioQueue<String> getTaskQueue() {
            if (msgQueue == null) {
                synchronized (this) {
                    if (msgQueue == null) {
                        msgQueue = new AioFullWaitQueue<>(100);
                    }
                }
            }
            return msgQueue;
        }

        @Override
        public void runTask() {
            if (msgQueue.isEmpty()) {
                return;
            }
            String packet;
            while ((packet = msgQueue.poll()) != null) {
                System.out.println(packet);
            }
        }
    }
}
