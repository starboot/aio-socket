package cn.starboot.socket.core.maintain.impl;

import cn.starboot.socket.core.maintain.AbstractMultiMaintain;
import cn.starboot.socket.core.enums.MaintainEnum;

/**
 * token业务逻辑类(一对多)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class Tokens extends AbstractMultiMaintain {

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.TOKEN;
	}
}
