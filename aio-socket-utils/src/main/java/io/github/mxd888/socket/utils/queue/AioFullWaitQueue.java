package io.github.mxd888.socket.utils.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * mxd
 * @param <T>
 */
public class AioFullWaitQueue<T> implements FullWaitQueue<T> {

    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

    private final Semaphore capacity;

    private final Integer total;

    private final boolean useSingleProducer;

    public AioFullWaitQueue(Integer capacity, boolean useSingleProducer) {
        this.capacity = new Semaphore(capacity);
        this.useSingleProducer = useSingleProducer;
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

    /**
     * 拿的时候不需要判空，直接拿，完事看拿到的是不是空
     * @return t
     */
    @Override
    public T poll() {
        if (isEmpty()) {
            return null;
        }
        T poll = queue.poll();
        capacity.release();
        return poll;
    }

    @Override
    public void clear() {
        queue.clear();
    }

    /**
     * 改善后的size()性能大幅提高
     * @return 0
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
    public boolean getProducerModel() {
        return useSingleProducer;
    }
}
