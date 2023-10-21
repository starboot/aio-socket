package cn.starboot.socket.core.utils.concurrent.collection;

import cn.starboot.socket.core.utils.concurrent.AbstractConcurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class AbstractConcurrentWithCollection<T, E> extends AbstractConcurrent<T> {

	private static final long serialVersionUID = 5756706150253873189L;

	public AbstractConcurrentWithCollection(T object) {
		super(object);
	}

	public boolean isEmpty() {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		isEmpty(consumer);
		return result.get();
	}

	public abstract void isEmpty(Consumer<Boolean> callBackFunction);

	public boolean contains(E object) {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		contains(object, consumer);
		return result.get();
	}

	public abstract void contains(E object, Consumer<Boolean> callBackFunction);

	public abstract void iterator(Consumer<Iterator<E>> callBackFunction);

	public abstract void toArray(Consumer<Object[]> callBackFunction);

	public abstract <B> void toArray(B[] a, Consumer<B[]> callBackFunction);

	public boolean add(E e) {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		add(e, consumer);
		return result.get();
	}

	public abstract void add(E e, Consumer<Boolean> callBackFunction);

	public boolean remove(E e) {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		remove(e, consumer);
		return result.get();
	}

	public abstract void remove(E e, Consumer<Boolean> callBackFunction);

	public boolean containsAll(Collection<?> c) {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		containsAll(c, consumer);
		return result.get();
	}

	public abstract void containsAll(Collection<?> c, Consumer<Boolean> callBackFunction);

	public boolean addAll(Collection<? extends E> c) {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		addAll(c, consumer);
		return result.get();
	}

	public abstract void addAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction);

	public boolean retainAll(Collection<? extends E> c) {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		retainAll(c, consumer);
		return result.get();
	}

	public abstract void retainAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction);

	public boolean removeAll(Collection<? extends E> c) {
		AtomicBoolean result = new AtomicBoolean(false);
		Consumer<Boolean> consumer = result::set;
		removeAll(c, consumer);
		return result.get();
	}

	public abstract void removeAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction);
}
