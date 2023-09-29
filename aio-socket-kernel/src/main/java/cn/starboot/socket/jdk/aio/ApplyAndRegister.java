package cn.starboot.socket.jdk.aio;

@FunctionalInterface
public interface ApplyAndRegister<T> {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	T applyAndRegister();
}
