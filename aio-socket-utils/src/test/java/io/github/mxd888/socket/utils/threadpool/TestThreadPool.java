package io.github.mxd888.socket.utils.threadpool;

import io.github.mxd888.socket.utils.ThreadUtils;
import io.github.mxd888.socket.utils.pool.thread.AbstractQueueRunnable;
import io.github.mxd888.socket.utils.pool.thread.SynThreadPoolExecutor;
import io.github.mxd888.socket.utils.queue.AioFullWaitQueue;
import io.github.mxd888.socket.utils.queue.FullWaitQueue;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池测试
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
        SynThreadPoolExecutor aioExecutor = ThreadUtils.getAioExecutor();
        testSynRunnable testSynRunnable = new testSynRunnable(aioExecutor);
        for (int i = 0; i < 100; i++) {
            testSynRunnable.addMsg("我是" + i +"号");
            testSynRunnable.execute();
        }
    }

    /**
     * 异步线程池；执行顺序是随机的，没有先来后到
     */
    private static void testThreadPoolExecutor() {
        ThreadPoolExecutor groupExecutor = ThreadUtils.getGroupExecutor();

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

    static class testSynRunnable extends AbstractQueueRunnable<String> {

        private FullWaitQueue<String> msgQueue = null;

        protected testSynRunnable(Executor executor) {
            super(executor);
        }

        @Override
        public FullWaitQueue<String> getMsgQueue() {
            if (msgQueue == null) {
                synchronized (this) {
                    if (msgQueue == null) {
                        msgQueue = new AioFullWaitQueue<>(100, false);
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
