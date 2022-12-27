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
package cn.starboot.socket.utils.cache;

import java.io.Serializable;
import java.util.UUID;

public class CacheChangedVo implements Serializable {

	private static final long serialVersionUID = 1546804469064012259L;

	public static final String CLIENTID = UUID.randomUUID().toString();

	private String cacheName;

	private String key;

	private String clientId = CLIENTID;

	private CacheChangeType type;

	public CacheChangedVo() {
		super();
	}

	public CacheChangedVo(String cacheName, CacheChangeType type) {
		this();
		this.cacheName = cacheName;
		this.type = type;
	}

	public CacheChangedVo(String cacheName, String key, CacheChangeType type) {
		this();
		this.cacheName = cacheName;
		this.key = key;
		this.type = type;
	}

	public String getCacheName() {
		return cacheName;
	}

	public String getClientId() {
		return clientId;
	}

	public String getKey() {
		return key;
	}

	public CacheChangeType getType() {
		return type;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setType(CacheChangeType type) {
		this.type = type;
	}
}
