/*******************************************************************************
 * Copyright (c) 2017-2019, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: Protocol.java
 * Date: 2019-12-31
 * Author: sandao (zhengjunweimail@163.com)
 *
 ******************************************************************************/
package cn.starboot.http.server.intercept;

import java.lang.reflect.Method;

public interface MethodInvocation {
	Method getMethod();

	Object[] getArguments();

	Object getThis();

	Object proceed() throws Throwable;
}
