package cn.starboot.socket.core.functional;

import cn.starboot.socket.core.utils.pool.memory.MemoryUnit;

@FunctionalInterface
public interface MemoryUnitSupplier {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	MemoryUnit applyMemoryUnit();
}
