package io.github.mxd888.socket.utils.queue;

import io.github.mxd888.socket.utils.ThreadUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
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
 * 硬件指标：PC(CPU: Intel(R) Core(TM) i7-9700 @ 3.00GHz; Memory: 16GB; 无固态; 机械盘: TOSHIBA DT01ACA200 联想硬盘)
 */
public class AioFullWaitQueueTest {

    private static final int offerThreadNum = 10;

    private static final int processThreadNum = 1;

    private static  final int ONE_MILLION = 1000000;

    private static final int MsgTotalNum = offerThreadNum * ONE_MILLION;

    private static final LongAdder processMsgNum = new LongAdder();

    private static final CountDownLatch l = new CountDownLatch(MsgTotalNum);

    public static void main(String[] args) throws InterruptedException {
        FullWaitQueue<String> aioFullWaitQueue = new AioFullWaitQueue<>(512, false);
        ThreadPoolExecutor groupExecutor = ThreadUtils.getGroupExecutor(11);
        AddQueue addQueue = new AddQueue("add aio-socket", aioFullWaitQueue);
        PollQueue pollQueue = new PollQueue(aioFullWaitQueue);
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

    /**
     * 生产者Java对象
     */
    static class AddQueue implements Runnable{

        String s;

        FullWaitQueue<String> queue;

        public AddQueue(String s, FullWaitQueue<String> queue) {
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

        FullWaitQueue<String> queue;

        public PollQueue(FullWaitQueue<String> queue) {
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

}
