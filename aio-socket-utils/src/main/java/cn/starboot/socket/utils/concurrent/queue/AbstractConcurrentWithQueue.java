package cn.starboot.socket.utils.concurrent.queue;

import cn.starboot.socket.utils.concurrent.collection.AbstractConcurrentWithCollection;
import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithWriteHandler;

import java.util.*;
import java.util.function.Consumer;

abstract class AbstractConcurrentWithQueue<E> extends AbstractConcurrentWithCollection<Queue<E>, E> {

	private static final long serialVersionUID = -4290317756679662354L;

	AbstractConcurrentWithQueue(Queue<E> object) {
		super(object);
	}

	/**
	 * Inserts the specified element into this queue if it is possible to do
	 * so immediately without violating capacity restrictions.
	 * When using a capacity-restricted queue, this method is generally
	 * preferable to {@link #add}, which can fail to insert an element only
	 * by throwing an exception.
	 *
	 * @param e the element to add
	 * // @return {@code true} if the element was added to this queue, else
	 *         {@code false}
	 * @throws ClassCastException if the class of the specified element
	 *         prevents it from being added to this queue
	 * @throws NullPointerException if the specified element is null and
	 *         this queue does not permit null elements
	 * @throws IllegalArgumentException if some property of this element
	 *         prevents it from being added to this queue
	 */
	public abstract void offer(E e, Consumer<Boolean> callBackFunction);

	/**
	 * Retrieves and removes the head of this queue.  This method differs
	 * from {@link #poll poll} only in that it throws an exception if this
	 * queue is empty.
	 *
	 * // @return the head of this queue
	 * @throws NoSuchElementException if this queue is empty
	 */
	public abstract void remove(Consumer<E> callBackFunction);

	/**
	 * Retrieves and removes the head of this queue,
	 * or returns {@code null} if this queue is empty.
	 *
	 * // @return the head of this queue, or {@code null} if this queue is empty
	 */
	public abstract void poll(Consumer<E> callBackFunction);

	/**
	 * Retrieves, but does not remove, the head of this queue.  This method
	 * differs from {@link #peek peek} only in that it throws an exception
	 * if this queue is empty.
	 *
	 * // @return the head of this queue
	 * @throws NoSuchElementException if this queue is empty
	 */
	public abstract void element(Consumer<E> callBackFunction);

	/**
	 * Retrieves, but does not remove, the head of this queue,
	 * or returns {@code null} if this queue is empty.
	 *
	 * // @return the head of this queue, or {@code null} if this queue is empty
	 */
	public abstract void peek(Consumer<E> callBackFunction);

	@Override
	public void size(Consumer<Integer> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.size()));
	}

	@Override
	public void isEmpty(Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.isEmpty()));
	}

	@Override
	public void contains(E object, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.contains(object)));
	}

	@Override
	public void iterator(Consumer<Iterator<E>> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.iterator()));
	}

	@Override
	public void toArray(Consumer<Object[]> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.toArray()));
	}

	@Override
	public <T> void toArray(T[] a, Consumer<T[]> callBackFunction) {
		if (a == null)
			return;
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.toArray(a)));
	}

	@Override
	public void add(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.add(e)));
	}

	@Override
	public void remove(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.remove(e)));
	}

	@Override
	public void containsAll(Collection<?> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.containsAll(c)));
	}

	@Override
	public void addAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.addAll(c)));
	}

	@Override
	public void retainAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.retainAll(c)));
	}

	@Override
	public void removeAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.removeAll(c)));
	}

	@Override
	public void clear() {
		handle((ConcurrentWithReadHandler<Queue<E>>) Queue::clear);
	}
}
