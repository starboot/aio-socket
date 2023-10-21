/*
 *    Copyright 2020 The t-io Project
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
package cn.starboot.socket.core.utils.cache;

import cn.starboot.socket.core.utils.json.JsonUtil;

import java.io.Serializable;

/**
 * 所有使用缓存的都要继承此对象，或者重写自己的对象中toString方法，按照如下模版
 *
 * 对缓存的封装，包括Redis、Caffeine和j2cache，
 * 此部分源代码出自talent-tan的开源项目t-io。
 *
 * @author t-io: https://gitee.com/tywo45/t-io.git
 * @author MDong
 */
public class CacheObj implements Serializable {

	/* uid */
	private static final long serialVersionUID = 7405927559870599104L;

	/**
	 * 不要重写此方法
	 * 不需要担心转化性能，fast json2性能无敌
	 *
	 * @return 所有缓存对象均使用JSON格式存储
	 */
	@Override
	public String toString() {
		return JsonUtil.toJSONString(this);
	}
}
