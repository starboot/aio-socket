/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.http.server;

import cn.starboot.http.server.handler.RestHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class RestfulBootstrap {
	private final HttpBootstrap httpBootstrap = new HttpBootstrap();
	private final RestHandler restHandler;
	private static final HttpServerHandler DEFAULT_HANDLER = new HttpServerHandler() {
		private final byte[] BYTES = "hello smart-http-rest".getBytes();

		@Override
		public void handle(HttpRequest request, HttpResponse response) throws IOException {
			response.getOutputStream().write(BYTES);
		}
	};

	private RestfulBootstrap(HttpServerHandler defaultHandler) {
		if (defaultHandler == null) {
			throw new NullPointerException();
		}
		this.restHandler = new RestHandler(defaultHandler);
		httpBootstrap.addHandler(restHandler);
	}

	public static HttpBootstrap controller(List<Class<?>> controllers) throws Exception {
		RestfulBootstrap restfulBootstrap = getInstance();
		for (Class<?> controller : controllers) {
			restfulBootstrap.controller(controller);
		}
		return restfulBootstrap.httpBootstrap;
	}

	public static RestfulBootstrap getInstance() throws Exception {
		return getInstance(DEFAULT_HANDLER);
	}

	public static RestfulBootstrap getInstance(HttpServerHandler defaultHandler) throws Exception {
		return new RestfulBootstrap(defaultHandler);
	}

	public static HttpBootstrap controller(Class<?>... controllers) throws Exception {
		return controller(Arrays.asList(controllers));
	}

	public RestfulBootstrap controller(Class<?> controllerClass) throws Exception {
		restHandler.addController(controllerClass);
		return this;
	}

	public RestfulBootstrap controller(Object controller) throws Exception {
		restHandler.addController(controller);
		return this;
	}

	public RestfulBootstrap inspect(BiConsumer<HttpRequest, HttpResponse> consumer) {
		restHandler.setInspect(consumer);
		return this;
	}

	public HttpBootstrap bootstrap() {
		return httpBootstrap;
	}
}
