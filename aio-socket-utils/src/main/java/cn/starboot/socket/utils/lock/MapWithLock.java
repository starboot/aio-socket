/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket.utils.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapWithLock<K, V> extends ObjWithLock<Map<K, V>> {
	private static final long	serialVersionUID	= -652862323697152866L;
	private static final Logger	log					= LoggerFactory.getLogger(MapWithLock.class);

	public MapWithLock() {
		this(new HashMap<>());
	}

	public MapWithLock(int initCapacity) {
		this(new HashMap<>(initCapacity));
	}

	public MapWithLock(Map<K, V> map) {
		super(map);
	}

	public MapWithLock(Map<K, V> map, ReentrantReadWriteLock lock) {
		super(map, lock);
	}

	public V put(K key, V value) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.put(key, value);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return null;
	}

	/**
	 * 如果key值已经存在，则不会把新value put进去
	 * 如果key值不存在，此方法同put(key, value)
	 * @param key
	 * @param value
	 * @return
	 */
	public V putIfAbsent(K key, V value) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			V oldValue = map.putIfAbsent(key, value);
			if (oldValue == null) {
				return value;
			} else {
				return oldValue;
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return null;
	}

	public void putAll(Map<K, V> otherMap) {
		if (otherMap == null || otherMap.isEmpty()) {
			return;
		}

		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			map.putAll(otherMap);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

	public V remove(K key) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.remove(key);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return null;
	}

	public void clear() {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			map.clear();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

	public V get(K key) {
		ReadLock readLock = this.readLock();
		readLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.get(key);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			readLock.unlock();
		}
		return null;
	}

	public int size() {
		ReadLock readLock = this.readLock();
		readLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.size();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 *
	 * @return 如果没值，则返回null，否则返回一个新map
	 */
	public Map<K, V> copy() {
		ReadLock readLock = readLock();
		readLock.lock();
		try {
			if (this.getObj().size() > 0) {
				return new HashMap<>(getObj());
			}
			return null;
		} finally {
			readLock.unlock();
		}
	}

}
