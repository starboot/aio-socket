package cn.starboot.socket.utils.concurrent.collection;

import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithWriteHandler;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class ConcurrentWithSet<E> extends AbstractConcurrentWithCollection<Set<E>, E> {

	private static final long serialVersionUID = 863617519833906818L;

	public ConcurrentWithSet(Set<E> object) {
		super(object);
	}

	@Override
	public void size(Consumer<Integer> callBackFunction) {
		handle((ConcurrentWithReadHandler<Set<E>>) es -> callBackFunction.accept(es.size()));
	}

	@Override
	public void isEmpty(Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Set<E>>) es -> callBackFunction.accept(es.isEmpty()));
	}

	@Override
	public void contains(E object, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Set<E>>) es -> callBackFunction.accept(es.contains(object)));
	}

	@Override
	public void iterator(Consumer<Iterator<E>> callBackFunction) {
		handle((ConcurrentWithReadHandler<Set<E>>) es -> callBackFunction.accept(es.iterator()));
	}

	@Override
	public void toArray(Consumer<Object[]> callBackFunction) {
		handle((ConcurrentWithReadHandler<Set<E>>) es -> callBackFunction.accept(es.toArray()));
	}

	@Override
	public <T> void toArray(T[] a, Consumer<T[]> callBackFunction) {
		if (a == null)
			return;
		handle((ConcurrentWithReadHandler<Set<E>>) es -> callBackFunction.accept(es.toArray(a)));
	}

	@Override
	public void add(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Set<E>>) es -> callBackFunction.accept(es.add(e)));
	}

	@Override
	public void remove(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Set<E>>) es -> callBackFunction.accept(es.remove(e)));
	}

	@Override
	public void containsAll(Collection<?> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Set<E>>) es -> callBackFunction.accept(es.containsAll(c)));
	}

	@Override
	public void addAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Set<E>>) es -> callBackFunction.accept(es.addAll(c)));
	}

	@Override
	public void retainAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Set<E>>) es -> callBackFunction.accept(es.retainAll(c)));
	}

	@Override
	public void removeAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Set<E>>) es -> callBackFunction.accept(es.removeAll(c)));
	}

	@Override
	public void clear() {
		handle((ConcurrentWithReadHandler<Set<E>>) Set::clear);
	}
}
