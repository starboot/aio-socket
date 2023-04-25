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

import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.maintain.AbstractSingleMaintain;
import cn.starboot.socket.maintain.MaintainEnum;

import java.util.Objects;

/**
 * ID业务逻辑类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Ids extends AbstractSingleMaintain {

	@Override
	public final synchronized boolean join(String id, ChannelContext context) {
		return Objects.nonNull(getSingleMaintainMap().put(id, context));
	}

	@Override
	public boolean remove(String id, ChannelContext context) {
		ChannelContext singleMaintainMapChannelContext = getSingleMaintainMap().get(id);
		if (Objects.nonNull(singleMaintainMapChannelContext)
				&& Objects.equals(singleMaintainMapChannelContext,context)) {
			return Objects.nonNull(getSingleMaintainMap().remove(id));
		}else return Objects.isNull(singleMaintainMapChannelContext);
	}

	@Override
	public boolean removeAll(String id, ChannelContext context) {
		return remove(id, context);
	}

	@Override
	public <T> T get(String id, Class<T> t) {
		ChannelContext singleMaintainMapChannelContext = getSingleMaintainMap().get(id);
		return Objects.isNull(singleMaintainMapChannelContext) ? null : (T) singleMaintainMapChannelContext;
	}

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.USER_ID;
	}
}
