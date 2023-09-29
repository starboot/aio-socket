package cn.starboot.socket.jdk.aio;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface ApplyAndRegister<T> {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	T apply();

	/**
	 * Returns a composed {@code Consumer} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the
	 * composed operation.  If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code Consumer} that performs in sequence this
	 * operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 */
	default Supplier<T> andRegister(Consumer<? super T> after) {
		Objects.requireNonNull(after);

		return () -> { T t1 = apply(); after.accept(t1); return t1; };
	}
}
