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

public class Attachment {

	/**
	 * 附件值
	 */
	private Object[] values = new Object[8];

	/**
	 * 存储附件
	 *
	 * @param key   附件Key
	 * @param value 附件值
	 * @param <T>   附件值
	 */
	public <T> void put(AttachKey<T> key, T value) {
		int index = key.getIndex();
		if (index > values.length) {
			Object[] old = values;
			int i = 1;
			do {
				i <<= 1;
			} while (i < index);
			values = new Object[i];
			System.arraycopy(old, 0, values, 0, old.length);
		}
		values[index] = value;
	}

	/**
	 * 获取附件对象
	 *
	 * @param key 附件Key
	 * @param <T> 附件值
	 * @return 附件值
	 */
	public <T> T get(AttachKey<T> key) {
		return (T) values[key.getIndex()];
	}

	/**
	 * 移除附件
	 *
	 * @param key 附件Key
	 * @param <T> 附件值
	 */
	public <T> void remove(AttachKey<T> key) {
		values[key.getIndex()] = null;
	}
}
