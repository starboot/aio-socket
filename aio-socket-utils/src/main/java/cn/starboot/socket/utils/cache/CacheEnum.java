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
package cn.starboot.socket.utils.cache;

/**
 * Cache 类型
 *
 * 对缓存的封装，包括Redis、Caffeine和j2cache，
 * 此部分源代码出自talent-tan的开源项目t-io。
 *
 * @author t-io: https://gitee.com/tywo45/t-io.git
 * @author MDong
 */
public enum CacheEnum {

	/**
	 * 获取Redis客户端
	 */
	REDIS,

	/**
	 * Caffeine
	 */
	CAFFEINE,

	/**
	 * J2Cache
	 */
	J2CACHE
}
