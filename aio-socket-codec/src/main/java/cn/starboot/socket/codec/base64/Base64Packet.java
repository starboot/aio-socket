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
package cn.starboot.socket.codec.base64;

import cn.starboot.socket.core.Packet;

public class Base64Packet extends Packet {

	/* uid */
	private static final long serialVersionUID = -7714821752025331906L;

	private final int length;

	private final byte[] data;

	public Base64Packet(int length, byte[] data) {
		this.length = length;
		this.data = data;
	}

	public int getLength() {
		return length;
	}

	public byte[] getData() {
		return data;
	}
}
