package cn.starboot.socket.core.utils.concurrent.map;

import cn.starboot.socket.core.utils.concurrent.AbstractConcurrent;
import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithReadHandler;
import cn.starboot.socket.core.utils.concurrent.handle.ConcurrentWithWriteHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 并发安全数据结构
 *
 * @param <K>
 * @param <V>
 */
public class ConcurrentWithMap<K, V> extends AbstractConcurrent<Map<K, V>> {

	private static final long serialVersionUID = -5069351493981319259L;

	public ConcurrentWithMap(Map<K, V> object) {
		super(object);
	}

	public void isEmpty(Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.isEmpty()));
	}

	public void containsKey(K key, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.containsKey(key)));
	}

	public void containsValue(V value, Consumer<Boolean> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.containsValue(value)));
	}

	public void get(K key, Consumer<V> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.get(key)));
	}

	public void put(K key, V value, Consumer<V> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.put(key, value)));
	}


	public void putAll(Map<? extends K, ? extends V> m) {
		handle((ConcurrentWithWriteHandler<Map<K, V>>) kvMap -> kvMap.putAll(m));
	}

	public void putIfAbsent(K key, V value, Consumer<V> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.putIfAbsent(key, value)));
	}

	public void remove(K key, Consumer<V> callBackFunction) {
		handle((ConcurrentWithWriteHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.remove(key)));
	}

	@Override
	public void size(Consumer<Integer> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.size()));
	}

	@Override
	public void clear() {
		handle((ConcurrentWithWriteHandler<Map<K, V>>) Map::clear);
	}

	public void keySet(Consumer<Set<K>> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.keySet()));
	}

	public void values(Consumer<Collection<V>> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.values()));
	}

	public void entrySet(Consumer<Set<Map.Entry<K, V>>> callBackFunction) {
		handle((ConcurrentWithReadHandler<Map<K, V>>) kvMap -> callBackFunction.accept(kvMap.entrySet()));
	}
}
