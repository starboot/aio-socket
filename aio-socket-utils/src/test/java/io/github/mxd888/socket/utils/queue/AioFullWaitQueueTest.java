package io.github.mxd888.socket.utils.queue;

import io.github.mxd888.socket.utils.ThreadUtils;
import io.github.mxd888.socket.utils.pool.thread.AbstractQueueRunnable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;

/**
 * 测试AioFullWaitQueue的性能；您如果有兴趣也可以实现接口FullWaitQueue来替换掉aio-socket自带的AioFullWaitQueue
 *
 * 测试参数：
 *      消息存放者： 10
 *      消息处理者： 1
 *      总消息：    消息存放者 * 1百万
 *
 * 作者MDong 测试结果：
 *      执行时间：1638 微秒
 *      存取消息数量：10000000
 *      约：500万/second
 *
 * 硬件参数：PC(CPU: Intel(R) Core(TM) i7-9700 @ 3.00GHz; Memory: 16GB; 无固态; 机械盘: TOSHIBA DT01ACA200 联想硬盘)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class AioFullWaitQueueTest {

    /**
     * 生产者数量
     */
    private static final int offerThreadNum = 10;

    /**
     * 消费者数量
     */
    private static final int processThreadNum = 1;

    /**
     * 一百万常数
     */
    private static  final int ONE_MILLION = 1000000;

    /**
     * 总消息量：生产者数量 * 一百万
     */
    private static final int MsgTotalNum = offerThreadNum * ONE_MILLION;

    /**
     * 已处理消息数量
     */
    private static final LongAdder processMsgNum = new LongAdder();

    /**
     * 程序计数器
     */
    private static final CountDownLatch l = new CountDownLatch(MsgTotalNum);

    /**
     * main方法：
     *      1、测试aio-socket线程池性能、测试Java自带线程池性能  ***  不要同时打开  ***
     *      2、author by MDong
     *
     * @param args                  启动参数，如 java -jar AioFullWaitQueueTest.jar args0 args1 ...
     * @throws InterruptedException CountDownLatch wait超时异常
     */
    public static void main(String[] args) throws InterruptedException {

        // 队列默认消息体
        String message = "aio-socket ThreadPool";

        // 满等待常数 达到该数字后；若入队；则等待
        int capacity = 512;

        // 建立aio-socket满等待队列
        AioQueue<String> aioAioQueue = new AioFullWaitQueue<>(capacity);

        // 测试(aio-socket 线程池 + aio-socket 满等待队列)性能
        testAioExecutor(aioAioQueue, message);

        // 测试(Java8 自带线程池 + aio-socket 满等待队列)性能
//        testGroupExecutor(aioFullWaitQueue, message);
    }

    private static void testGroupExecutor(AioQueue<String> aioAioQueue, String message)
            throws InterruptedException {
        ExecutorService groupExecutor = ThreadUtils.getGroupExecutor(offerThreadNum + processThreadNum);
        AddQueue addQueue = new AddQueue(message, aioAioQueue);
        PollQueue pollQueue = new PollQueue(aioAioQueue);
        // 计时开始
        long start = System.currentTimeMillis();
        // 启动消息提供者
        for (int i = 0; i < offerThreadNum; i++) {
            groupExecutor.execute(addQueue);
        }
        // 启动消息消费者
        for (int i = 0; i < processThreadNum; i++) {
            groupExecutor.execute(pollQueue);
        }
        l.await();
        // 计时结束
        long end = System.currentTimeMillis();
        System.out.println("执行时间：" + (end - start) + "\r\n" +
                "存取消息数量：" + processMsgNum.longValue());
        // 关闭线程池
        groupExecutor.shutdown();
    }

    private static void testAioExecutor(AioQueue<String> aioAioQueue, String message)
            throws InterruptedException {
        ExecutorService aioExecutor = ThreadUtils.getAioExecutor();
        AioPollQueue synPollQueue = new AioPollQueue(aioAioQueue, aioExecutor);
        // 计时开始
        long start = System.currentTimeMillis();
        // 启动消息提供者
        for (int i = 0; i < offerThreadNum; i++) {
            new AioAddQueue(message, aioExecutor, synPollQueue).execute();
        }
        // 启动消息消费者
        // 消费者有生产者自动唤醒
        l.await();
        // 计时结束
        long end = System.currentTimeMillis();
        System.out.println("执行时间：" + (end - start) + "\r\n" +
                "存取消息数量：" + processMsgNum.longValue());
        // 关闭线程池
        aioExecutor.shutdown();
    }

    /**
     * 生产者Java对象
     */
    static class AddQueue implements Runnable{

        String s;

        AioQueue<String> queue;

        public AddQueue(String s, AioQueue<String> queue) {
            this.s = s;
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < ONE_MILLION; i++) {
                queue.offer(s);
            }
        }
    }

    /**
     * 消费者Java对象
     */
    static class PollQueue implements Runnable {

        AioQueue<String> queue;

        public PollQueue(AioQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            int i = 0;
            while (!queue.isEmpty() || i < MsgTotalNum) {
                String poll = queue.poll();
                if (poll != null && poll.length() > 0) {
                    processMsgNum.increment();
                    l.countDown();
                    i++;
                }
            }
        }
    }

    /**
     * 同步生产者Java对象
     */
    static class AioAddQueue extends AbstractQueueRunnable<String>{

        String s;

        AbstractQueueRunnable<String> aioQueue;

        public AioAddQueue(String s, Executor executor, AbstractQueueRunnable<String> pollQueue) {
            super(executor);
            this.s = s;
            this.aioQueue = pollQueue;
        }

        @Override
        public AioQueue<String> getTaskQueue() {
            return null;
        }

        @Override
        public void runTask() {
            for (int i = 0; i < ONE_MILLION; i++) {
                aioQueue.addMsg(s);
                aioQueue.execute();
            }
        }
    }

    /**
     * 同步消费者Java对象
     */
    static class AioPollQueue extends AbstractQueueRunnable<String> {

        AioQueue<String> queue;

        public AioPollQueue(AioQueue<String> queue, Executor executor) {
            super(executor);
            this.queue = queue;
        }

        @Override
        public AioQueue<String> getTaskQueue() {
            return queue;
        }

        @Override
        public void runTask() {
            if (queue.isEmpty()) {
                return;
            }
            while (queue.poll() != null) {
                processMsgNum.increment();
                l.countDown();
            }
        }
    }


}
