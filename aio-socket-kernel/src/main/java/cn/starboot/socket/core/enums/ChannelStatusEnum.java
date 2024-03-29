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
package cn.starboot.socket.core.enums;

/**
 * ChannelContext状态
 *
 * Created by DELL(mxd) on 2022/12/30 14:38
 */
public enum ChannelStatusEnum {

	/*
	通道已关闭
	 */
	CHANNEL_STATUS_CLOSED,

	/*
	通道正在关闭中
	 */
	CHANNEL_STATUS_CLOSING,

	/*
	通道可用
	 */
	CHANNEL_STATUS_ENABLED
}
