package cn.starboot.socket.maintain.impl;

import cn.starboot.socket.maintain.AbstractSingleMaintain;
import cn.starboot.socket.enums.MaintainEnum;

/**
 * 客户节点逻辑类(一对一)
 * 将用户通道与其IP+端口号进行唯一绑定
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
