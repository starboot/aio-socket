package cn.starboot.socket.maintain.impl;

import cn.starboot.socket.maintain.AbstractSingleMaintain;
import cn.starboot.socket.maintain.MaintainEnum;

/**
 * ID业务逻辑类(一对一)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClientNodes extends AbstractSingleMaintain {

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.CLIENT_NODE_ID;
	}
}
