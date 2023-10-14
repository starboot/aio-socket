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

public final class MethodInvocationImpl implements MethodInvocation {
	private final Method method;
	private final Object[] args;
	private final Object object;

	public MethodInvocationImpl(Method method, Object[] args, Object object) {
		this.method = method;
		this.args = args;
		this.object = object;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public Object[] getArguments() {
		return args;
	}

	@Override
	public Object getThis() {
		return object;
	}

	@Override
	public Object proceed() throws Throwable {
		return method.invoke(object, args);
	}

}
