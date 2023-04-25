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
package cn.starboot.socket.maintain.impl;

import cn.starboot.socket.maintain.AbstractSingleMaintain;
import cn.starboot.socket.maintain.MaintainEnum;

/**
 * ID业务逻辑类(一对一)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Ids extends AbstractSingleMaintain {

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.USER_ID;
	}

}
