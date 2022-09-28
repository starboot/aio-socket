package io.github.mxd888.socket.utils.queue;

/**
 * 满员等待队列
 */
public interface FullWaitQueue<T> {
    /**
     * write
     *  向队列尾添加一个元素，如果队列已经满了，则等待一段时间
     * @param t
     * @return
     * @author tanyaowu
     */
    boolean offer(T t);

    /**
     * read
     * Retrieves and removes the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    T poll();

    void clear();

    int size();

    boolean isEmpty();

    boolean getProducerModel();
}
