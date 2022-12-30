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
import cn.starboot.http.common.enums.HttpMethodEnum;
import cn.starboot.http.common.enums.HttpStatus;
import cn.starboot.http.common.logging.Logger;
import cn.starboot.http.common.logging.LoggerFactory;
import cn.starboot.http.common.utils.DateUtils;
import cn.starboot.http.common.utils.Mimetypes;
import cn.starboot.http.common.utils.StringUtils;
import cn.starboot.http.server.HttpResponse;
import cn.starboot.http.server.HttpServerHandler;
import cn.starboot.http.server.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class HttpStaticResourceHandler extends HttpServerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStaticResourceHandler.class);
    private static final int READ_BUFFER = 1024 * 1024;
    private static final String URL_404 =
            "<html>" +
                    "<head>" +
                    "<title>smart-http 404</title>" +
                    "</head>" +
                    "<body><h1>smart-http 找不到你所请求的地址资源，404</h1></body>" +
                    "</html>";

    private final File baseDir;

    public HttpStaticResourceHandler(String baseDir) {
        this.baseDir = new File(new File(baseDir).getAbsolutePath());
        if (!this.baseDir.isDirectory()) {
            throw new RuntimeException(baseDir + " is not a directory");
        }
        if(LOGGER.isInfoEnabled())
            LOGGER.info("dir is:{}", this.baseDir.getAbsolutePath());
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = request.getRequestURI();
        String method = request.getMethod();
        if (StringUtils.endsWith(fileName, "/")) {
            fileName += "index.html";
        }
        if(LOGGER.isInfoEnabled())
            LOGGER.info("请求URL: " + fileName);
        File file = new File(baseDir, URLDecoder.decode(fileName, "utf8"));
        //404
        if (!file.isFile()) {
            LOGGER.warn("file: {} not found!", request.getRequestURI());
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setHeader(HeaderNameEnum.CONTENT_TYPE.getName(), "text/html; charset=utf-8");

            if (!HttpMethodEnum.HEAD.getMethod().equals(method)) {
                response.write(URL_404.getBytes());
            }
            return;
        }
        //304
        Date lastModifyDate = new Date(file.lastModified());
        try {
            String requestModified = request.getHeader(HeaderNameEnum.IF_MODIFIED_SINCE.getName());
            if (StringUtils.isNotBlank(requestModified) && lastModifyDate.getTime() <= DateUtils.parseLastModified(requestModified).getTime()) {
                response.setHttpStatus(HttpStatus.NOT_MODIFIED);
                return;
            }
        } catch (Exception e) {
            LOGGER.error("exception", e);
        }
        response.setHeader(HeaderNameEnum.LAST_MODIFIED.getName(), DateUtils.formatLastModified(lastModifyDate));


        String contentType = Mimetypes.getInstance().getMimetype(file);
        response.setHeader(HeaderNameEnum.CONTENT_TYPE.getName(), contentType + "; charset=utf-8");
        //HEAD不输出内容
        if (HttpMethodEnum.HEAD.getMethod().equals(method)) {
            return;
        }

        if (!file.getName().endsWith("html") && !file.getName().endsWith("htm")) {
            response.setContentLength((int) file.length());
        }


        FileInputStream fis = new FileInputStream(file);
        FileChannel fileChannel = fis.getChannel();
        long fileSize = fileChannel.size();
        long readPos = 0;
        while (readPos < fileSize) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, readPos, fileSize - readPos > READ_BUFFER ? READ_BUFFER : fileSize - readPos);
            readPos += mappedByteBuffer.remaining();
            byte[] data = new byte[mappedByteBuffer.remaining()];
            mappedByteBuffer.get(data);
            response.write(data);
        }
        fileChannel.close();
        fis.close();
    }
}
