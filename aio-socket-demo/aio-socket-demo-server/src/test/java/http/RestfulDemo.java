package http;

import cn.starboot.http.server.RestfulBootstrap;
import cn.starboot.http.server.annotation.Controller;
import cn.starboot.http.server.annotation.RequestMapping;
import cn.starboot.http.server.annotation.RequestMethod;

@Controller
public class RestfulDemo {

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String helloworld() {
		return "hello world";
	}

	public static void main(String[] args) throws Exception {
		RestfulBootstrap bootstrap = RestfulBootstrap.getInstance().controller(RestfulDemo.class);
		bootstrap.bootstrap().setPort(8080).start();
	}
}
