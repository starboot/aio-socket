package cn.starboot.socket.utils.concurrent.collection;

import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithWriteHandler;

import java.util.*;
import java.util.function.Consumer;

public class ConcurrentWithList<E> extends AbstractConcurrentWithCollection<List<E>, E> {

	private static final long serialVersionUID = 6662959285494057624L;

	public ConcurrentWithList(List<E> object) {
		super(object);
	}

	@Override
	public void size(Consumer<Integer> callBackFunction) {
		handle((ConcurrentWithReadHandler<List<E>>) es -> callBackFunction.accept(es.size()));
	}

	@Override
	public void isEmpty(Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<List<E>>) es -> callBackFunction.accept(es.isEmpty()));
	}

	@Override
	public void contains(E object, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<List<E>>) es -> callBackFunction.accept(es.contains(object)));
	}

	@Override
	public void iterator(Consumer<Iterator<E>> callBackFunction) {
		handle((ConcurrentWithReadHandler<List<E>>) es -> callBackFunction.accept(es.iterator()));
	}

	@Override
	public void toArray(Consumer<Object[]> callBackFunction) {
		handle((ConcurrentWithReadHandler<List<E>>) es -> callBackFunction.accept(es.toArray()));
	}

	@Override
	public <T> void toArray(T[] a, Consumer<T[]> callBackFunction) {
		if (a == null)
			return;
		handle((ConcurrentWithReadHandler<List<E>>) es -> callBackFunction.accept(es.toArray(a)));
	}

	@Override
	public void add(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<List<E>>) es -> callBackFunction.accept(es.add(e)));
	}

	@Override
	public void remove(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<List<E>>) es -> callBackFunction.accept(es.remove(e)));
	}

	@Override
	public void containsAll(Collection<?> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<List<E>>) es -> callBackFunction.accept(es.containsAll(c)));
	}

	@Override
	public void addAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<List<E>>) es -> callBackFunction.accept(es.addAll(c)));
	}

	@Override
	public void retainAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<List<E>>) es -> callBackFunction.accept(es.retainAll(c)));
	}

	@Override
	public void removeAll(Collection<? extends E> c, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<List<E>>) es -> callBackFunction.accept(es.removeAll(c)));
	}

	@Override
	public void clear() {
		handle((ConcurrentWithReadHandler<List<E>>) List::clear);
	}
}
