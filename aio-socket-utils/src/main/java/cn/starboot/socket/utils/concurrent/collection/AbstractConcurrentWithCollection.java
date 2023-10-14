package cn.starboot.socket.utils.concurrent.collection;

import cn.starboot.socket.utils.concurrent.AbstractConcurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class AbstractConcurrentWithCollection<T, E> extends AbstractConcurrent<T> {

	private static final long serialVersionUID = 5756706150253873189L;

	public AbstractConcurrentWithCollection(T object) {
		super(object);
	}

	public abstract boolean isEmpty();

	public abstract void isEmpty(Consumer<Boolean> callBackFunction);

	public abstract void contains(E object, Consumer<Boolean> callBackFunction);

	public abstract void iterator(Consumer<Iterator<E>> callBackFunction);

	public abstract void toArray(Consumer<Object[]> callBackFunction);

	public abstract <B> void toArray(B[] a, Consumer<B[]> callBackFunction);

	public abstract void add(E e, Consumer<Boolean> callBackFunction);

	public abstract void remove(E e, Consumer<Boolean> callBackFunction);

	public abstract void containsAll(Collection<?> c, Consumer<Boolean> callBackFunction);

	public abstract void addAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction);

	public abstract void retainAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction);

	public abstract void removeAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction);
}
