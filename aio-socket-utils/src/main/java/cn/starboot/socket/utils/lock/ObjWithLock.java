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

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自带读写锁的对象.
 */
public class ObjWithLock<T> implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(ObjWithLock.class);

	/* uid */
	private static final long serialVersionUID = 1259405044641449753L;

	private T obj;

	private final ReentrantReadWriteLock lock;

	public ObjWithLock(T obj) {
		this(obj, new ReentrantReadWriteLock());
	}

	public ObjWithLock(T obj, ReentrantReadWriteLock lock) {
		super();
		this.obj = obj;
		this.lock = lock;
	}

	public ReentrantReadWriteLock getLock() {
		return lock;
	}

	public WriteLock writeLock() {
		return lock.writeLock();
	}

	public ReadLock readLock() {
		return lock.readLock();
	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	/**
	 * 操作obj时，带上读锁
	 * @param readLockHandler 处理器
	 */
	public void handle(ReadLockHandler<T> readLockHandler) {
		ReadLock readLock = lock.readLock();
		readLock.lock();
		try {
			readLockHandler.handler(obj);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 操作obj时，带上写锁
	 * @param writeLockHandler 写处理器
	 */
	public void handle(WriteLockHandler<T> writeLockHandler) {
		WriteLock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			writeLockHandler.handler(obj);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

}
