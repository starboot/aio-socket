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
package cn.starboot.http.server.handler;

import cn.starboot.http.common.enums.HeaderNameEnum;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.utils.DateUtils;
import cn.starboot.http.common.utils.Mimetypes;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.HttpRequest;
import cn.starboot.http.server.HttpResponse;
import cn.starboot.http.server.HttpServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class StaticResourceHandler extends HttpServerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceHandler.class);
	private final Date lastModifyDate = new Date(System.currentTimeMillis() / 1000 * 1000);

	private final String lastModifyDateFormat = DateUtils.formatLastModified(lastModifyDate);

	@Override
	public void handle(HttpRequest request, HttpResponse response) throws IOException {
		String fileName = request.getRequestURI();

		if (StringUtils.endsWith(fileName, "/")) {
			fileName += "index.html";
		}

		//304
		try {
			String requestModified = request.getHeader(HeaderNameEnum.IF_MODIFIED_SINCE.getName());
			if (StringUtils.isNotBlank(requestModified) && lastModifyDate.getTime() <= DateUtils.parseLastModified(requestModified).getTime()) {
				response.setHttpStatus(HttpStatus.NOT_MODIFIED);
				return;
			}
		} catch (Exception e) {
			LOGGER.error("exception", e);
		}
		response.setHeader(HeaderNameEnum.LAST_MODIFIED.getName(), lastModifyDateFormat);

		try (InputStream inputStream = StaticResourceHandler.class.getClassLoader().getResourceAsStream("static" + fileName)) {
			if (inputStream == null) {
				response.setHttpStatus(HttpStatus.NOT_FOUND);
				return;
			}
			String contentType = Mimetypes.getInstance().getMimetype(fileName);
			response.setHeader(HeaderNameEnum.CONTENT_TYPE.getName(), contentType + "; charset=utf-8");
			byte[] bytes = new byte[1024];
			int length;
			while ((length = inputStream.read(bytes)) > 0) {
				response.getOutputStream().write(bytes, 0, length);
			}
		}
	}
}
