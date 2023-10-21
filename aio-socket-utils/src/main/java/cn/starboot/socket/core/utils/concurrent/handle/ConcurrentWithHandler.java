package cn.starboot.socket.core.utils.concurrent.handle;

public interface ConcurrentWithHandler<T> {

	void handler(T t) throws Exception;
}
