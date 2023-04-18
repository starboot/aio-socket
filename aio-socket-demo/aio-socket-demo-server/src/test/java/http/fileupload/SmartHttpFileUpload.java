package http.fileupload;

import cn.starboot.http.server.HttpRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

import java.io.IOException;

public class SmartHttpFileUpload extends FileUpload {

	public FileItemIterator getItemIterator(HttpRequest request)
			throws FileUploadException, IOException {
		return super.getItemIterator(new SmartHttpRequestContext(request));
	}
}
