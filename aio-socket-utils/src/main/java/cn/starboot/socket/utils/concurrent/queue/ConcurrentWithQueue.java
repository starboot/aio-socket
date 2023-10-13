package cn.starboot.socket.utils.concurrent.queue;

import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.utils.concurrent.handle.ConcurrentWithWriteHandler;

import java.util.Queue;
import java.util.function.Consumer;

public class ConcurrentWithQueue<E> extends AbstractConcurrentWithQueue<E>  {

	private static final long serialVersionUID = -9098233990576711585L;

	public ConcurrentWithQueue(Queue<E> object) {
		super(object);
	}

	@Override
	public void offer(E e, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.offer(e)));
	}

	@Override
	public void remove(Consumer<E> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.remove()));
	}

	@Override
	public void poll(Consumer<E> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Queue<E>>) es -> callBackFunction.accept(es.poll()));
	}

	@Override
	public void element(Consumer<E> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.element()));
	}

	@Override
	public void peek(Consumer<E> callBackFunction) {
		handle((ConcurrentWithReadHandler<Queue<E>>) es -> callBackFunction.accept(es.peek()));
	}


}
