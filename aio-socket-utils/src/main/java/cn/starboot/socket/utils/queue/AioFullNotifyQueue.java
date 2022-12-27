package cn.starboot.socket.utils.queue;

/**
 * 满通知队列
 * 1、无需等待
 * 2、可解决线程池在特定场景中高并发死锁问题(在超过线程池最大线程数的任务进行超大流量并发时会出现线程池死锁)
 * 3、性能更高(约提升30%)
 * 使用满通知队列则无此顾虑
 *
 * @param <T> 泛型对象
 */
public class AioFullNotifyQueue<T> extends AioFullWaitQueue<T> {

    public AioFullNotifyQueue(Integer capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(T t) {
        boolean acquire = capacity.tryAcquire();
        if (acquire) {
            return queue.offer(t);
        }
        return false;
    }
}
