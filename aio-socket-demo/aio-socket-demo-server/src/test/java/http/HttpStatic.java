package http;

import cn.starboot.http.server.HttpBootstrap;
import cn.starboot.http.server.RestfulBootstrap;
import cn.starboot.http.server.handler.HttpStaticResourceHandler;

public class HttpStatic {

	public static void main(String[] args) throws Exception {
		HttpBootstrap bootstrap = new HttpBootstrap();
		//配置HTTP消息处理管道
		bootstrap.addHandler(new HttpStaticResourceHandler("C:\\Users\\Administrator\\Desktop\\讲课PPT"));

		//设定服务器配置并启动
		bootstrap.start();
	}
}
