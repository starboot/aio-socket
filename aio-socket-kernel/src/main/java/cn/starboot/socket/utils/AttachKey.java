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
package cn.starboot.socket.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class AttachKey<T> {
	/**
	 * 支持附件数量上限
	 */
	public static final int MAX_ATTACHE_COUNT = 128;
	/**
	 * 缓存同名Key
	 */
	private static final ConcurrentMap<String, AttachKey> NAMES = new ConcurrentHashMap<>();
	/**
	 * 索引构造器
	 */
	private static final AtomicInteger INDEX_BUILDER = new AtomicInteger(0);
	/**
	 * 附件名称
	 */
	private final String key;
	/**
	 * 附件索引
	 */
	private final int index;

	private AttachKey(String key) {
		this.key = key;
		this.index = INDEX_BUILDER.getAndIncrement();
		if (this.index < 0 || this.index >= MAX_ATTACHE_COUNT) {
			throw new RuntimeException("too many attach key");
		}
	}

	public static <T> AttachKey<T> valueOf(String name, Class<T> t) {
		AttachKey<T> attachKey = NAMES.get(name);
		return attachKey == null ? NAMES.putIfAbsent(name, new AttachKey<T>(name)) : attachKey;
	}

	public String getKey() {
		return key;
	}


	int getIndex() {
		return index;
	}
}
