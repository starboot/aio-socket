package cn.starboot.socket.maintain.impl;

import cn.starboot.socket.maintain.AbstractMultiMaintain;
import cn.starboot.socket.maintain.MaintainEnum;

/**
 * IP业务逻辑类(一对多)
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Ips extends AbstractMultiMaintain {

	@Override
	public MaintainEnum getName() {
		return MaintainEnum.IP;
	}

}
