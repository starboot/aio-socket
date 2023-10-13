package cn.starboot.socket.utils.concurrent.handle;

public interface ConcurrentWithHandler<T> {

	void handler(T t) throws Exception;
}
