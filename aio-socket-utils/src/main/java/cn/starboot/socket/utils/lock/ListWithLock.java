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

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListWithLock<T> extends ObjWithLock<List<T>> {

	/* uid */
	private static final Logger	log					= LoggerFactory.getLogger(ListWithLock.class);

	private static final long serialVersionUID = -7543949226901252162L;

	public ListWithLock(List<T> list) {
		super(list);
	}

	public ListWithLock(List<T> list, ReentrantReadWriteLock lock) {
		super(list, lock);
	}

	public boolean add(T t) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			List<T> list = this.getObj();
			return list.add(t);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return false;
	}

	public void clear() {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			List<T> list = this.getObj();
			list.clear();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

	public boolean remove(T t) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			List<T> list = this.getObj();
			return list.remove(t);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return false;
	}

	public int size() {
		ReadLock readLock = this.readLock();
		readLock.lock();
		try {
			List<T> list = this.getObj();
			return list.size();
		} finally {
			readLock.unlock();
		}
	}
}
