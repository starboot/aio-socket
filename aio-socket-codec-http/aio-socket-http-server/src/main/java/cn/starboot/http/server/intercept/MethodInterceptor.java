package cn.starboot.http.server.intercept;

public interface MethodInterceptor {
	Object invoke(MethodInvocation invocation) throws Throwable;
}
