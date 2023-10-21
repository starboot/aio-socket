package cn.starboot.socket.core.maintain.impl;

import cn.starboot.socket.core.maintain.AbstractMultiMaintain;
import cn.starboot.socket.core.enums.MaintainEnum;

/**
 * 用户维持逻辑类(一对多)
 * 某一用户存在(PC,WEB,IOS,Android......)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Users extends AbstractMultiMaintain {

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.USER;
	}

}
