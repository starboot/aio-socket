package cn.starboot.socket.core.utils.concurrent.queue;

import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithWriteHandler;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;

public class ConcurrentWithDeque<E> extends ConcurrentWithQueue<E> {

	private static final long serialVersionUID = -8626443359709759220L;

	public ConcurrentWithDeque(Deque<E> object) {
		super(object);
	}

	/**
	 * Inserts the specified element at the front of this deque if it is
	 * possible to do so immediately without violating capacity restrictions,
	 * throwing an {@code IllegalStateException} if no space is currently
	 * available.  When using a capacity-restricted deque, it is generally
	 * preferable to use method {@link #offerFirst}.
	 *
	 * @param e the element to add
	 * @throws IllegalStateException if the element cannot be added at this
	 *         time due to capacity restrictions
	 * @throws ClassCastException if the class of the specified element
	 *         prevents it from being added to this deque
	 * @throws NullPointerException if the specified element is null and this
	 *         deque does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified
	 *         element prevents it from being added to this deque
	 */
	public void addFirst(E e) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				((Deque<E>) es).addFirst(e);
			}
		});
	}

	/**
	 * Inserts the specified element at the end of this deque if it is
	 * possible to do so immediately without violating capacity restrictions,
	 * throwing an {@code IllegalStateException} if no space is currently
	 * available.  When using a capacity-restricted deque, it is generally
	 * preferable to use method {@link #offerLast}.
	 *
	 * <p>This method is equivalent to {@link #add}.
	 *
	 * @param e the element to add
	 * @throws IllegalStateException if the element cannot be added at this
	 *         time due to capacity restrictions
	 * @throws ClassCastException if the class of the specified element
	 *         prevents it from being added to this deque
	 * @throws NullPointerException if the specified element is null and this
	 *         deque does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified
	 *         element prevents it from being added to this deque
	 */
	public void addLast(E e) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				((Deque<E>) es).addLast(e);
			}
		});
	}

	/**
	 * Inserts the specified element at the front of this deque unless it would
	 * violate capacity restrictions.  When using a capacity-restricted deque,
	 * this method is generally preferable to the {@link #addFirst} method,
	 * which can fail to insert an element only by throwing an exception.
	 *
	 * @param e the element to add
	 * // @callBack {@code true} if the element was added to this deque, else
	 *         {@code false}
	 * @throws ClassCastException if the class of the specified element
	 *         prevents it from being added to this deque
	 * @throws NullPointerException if the specified element is null and this
	 *         deque does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified
	 *         element prevents it from being added to this deque
	 */
	public void offerFirst(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).offerFirst(e));
			}
		});
	}

	/**
	 * Inserts the specified element at the end of this deque unless it would
	 * violate capacity restrictions.  When using a capacity-restricted deque,
	 * this method is generally preferable to the {@link #addLast} method,
	 * which can fail to insert an element only by throwing an exception.
	 *
	 * @param e the element to add
	 * // @callBack {@code true} if the element was added to this deque, else
	 *         {@code false}
	 * @throws ClassCastException if the class of the specified element
	 *         prevents it from being added to this deque
	 * @throws NullPointerException if the specified element is null and this
	 *         deque does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified
	 *         element prevents it from being added to this deque
	 */
	public void offerLast(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).offerLast(e));
			}
		});
	}

	/**
	 * Retrieves and removes the first element of this deque.  This method
	 * differs from {@link #pollFirst pollFirst} only in that it throws an
	 * exception if this deque is empty.
	 *
	 * // @return the head of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public void removeFirst(Consumer<E> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).removeFirst());
			}
		});
	}

	/**
	 * Retrieves and removes the last element of this deque.  This method
	 * differs from {@link #pollLast pollLast} only in that it throws an
	 * exception if this deque is empty.
	 *
	 * // @return the tail of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public void removeLast(Consumer<E> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).removeLast());
			}
		});
	}

	/**
	 * Retrieves and removes the first element of this deque,
	 * or returns {@code null} if this deque is empty.
	 *
	 * // @return the head of this deque, or {@code null} if this deque is empty
	 */
	public void pollFirst(Consumer<E> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).pollFirst());
			}
		});
	}

	/**
	 * Retrieves and removes the last element of this deque,
	 * or returns {@code null} if this deque is empty.
	 *
	 * // @return the tail of this deque, or {@code null} if this deque is empty
	 */
	public void pollLast(Consumer<E> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).pollLast());
			}
		});
	}

	/**
	 * Retrieves, but does not remove, the first element of this deque.
	 *
	 * This method differs from {@link #peekFirst peekFirst} only in that it
	 * throws an exception if this deque is empty.
	 *
	 * // @return the head of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public void getFirst(Consumer<E> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).getFirst());
			}
		});
	}

	/**
	 * Retrieves, but does not remove, the last element of this deque.
	 * This method differs from {@link #peekLast peekLast} only in that it
	 * throws an exception if this deque is empty.
	 *
	 * // @return the tail of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public void getLast(Consumer<E> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).getLast());
			}
		});
	}

	/**
	 * Retrieves, but does not remove, the first element of this deque,
	 * or returns {@code null} if this deque is empty.
	 *
	 * // @return the head of this deque, or {@code null} if this deque is empty
	 */
	public void peekFirst(Consumer<E> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).peekFirst());
			}
		});
	}

	/**
	 * Retrieves, but does not remove, the last element of this deque,
	 * or returns {@code null} if this deque is empty.
	 *
	 * // @return the tail of this deque, or {@code null} if this deque is empty
	 */
	public void peekLast(Consumer<E> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).peekLast());
			}
		});
	}

	/**
	 * Removes the first occurrence of the specified element from this deque.
	 * If the deque does not contain the element, it is unchanged.
	 * More formally, removes the first element {@code e} such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>
	 * (if such an element exists).
	 * Returns {@code true} if this deque contained the specified element
	 * (or equivalently, if this deque changed as a result of the call).
	 *
	 * @param o element to be removed from this deque, if present
	 * // @return {@code true} if an element was removed as a result of this call
	 * @throws ClassCastException if the class of the specified element
	 *         is incompatible with this deque
	 * (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this
	 *         deque does not permit null elements
	 * (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	public void removeFirstOccurrence(Object o, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).removeFirstOccurrence(o));
			}
		});
	}

	/**
	 * Removes the last occurrence of the specified element from this deque.
	 * If the deque does not contain the element, it is unchanged.
	 * More formally, removes the last element {@code e} such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>
	 * (if such an element exists).
	 * Returns {@code true} if this deque contained the specified element
	 * (or equivalently, if this deque changed as a result of the call).
	 *
	 * @param o element to be removed from this deque, if present
	 * // @return {@code true} if an element was removed as a result of this call
	 * @throws ClassCastException if the class of the specified element
	 *         is incompatible with this deque
	 * (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this
	 *         deque does not permit null elements
	 * (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	public void removeLastOccurrence(Object o, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> {
			if (es instanceof Deque) {
				callBackFunction.accept(((Deque<E>) es).removeLastOccurrence(o));
			}
		});
	}

}
