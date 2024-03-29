package cn.starboot.socket.core.maintain.impl;

import cn.starboot.socket.core.maintain.AbstractSingleMaintain;
import cn.starboot.socket.core.enums.MaintainEnum;

/**
 * ID业务逻辑类(一对一)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class BsIds extends AbstractSingleMaintain {

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.Bs_ID;
	}

}
