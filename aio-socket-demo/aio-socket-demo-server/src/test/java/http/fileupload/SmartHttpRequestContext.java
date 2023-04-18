package http.fileupload;

import cn.starboot.http.server.HttpRequest;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.UploadContext;

import java.io.IOException;
import java.io.InputStream;

class SmartHttpRequestContext implements UploadContext {

	private final HttpRequest request;

	public SmartHttpRequestContext(HttpRequest request) {
		this.request = request;
	}

	@Override
	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	@Deprecated
	public int getContentLength() {
		return request.getContentLength();
	}

	@Override
	public long contentLength() {
		long size;
		try {
			size = Long.parseLong(request.getHeader(FileUploadBase.CONTENT_LENGTH));
		} catch (NumberFormatException e) {
			size = request.getContentLength();
		}
		return size;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	@Override
	public String toString() {
		return ("ContentLength=%s, ContentType=%s"+
				Long.valueOf(this.contentLength())+
				this.getContentType());
	}

}
