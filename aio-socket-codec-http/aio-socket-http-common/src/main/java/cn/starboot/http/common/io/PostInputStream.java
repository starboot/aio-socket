package cn.starboot.http.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class PostInputStream extends InputStream {
    private InputStream inputStream;
    private int remaining;

    public PostInputStream(InputStream inputStream, int contentLength) {
        this.inputStream = inputStream;
        this.remaining = contentLength;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int size = inputStream.read(b, off, len);
        if (size > 0) {
            remaining -= size;
        }
        return size;
    }

    @Override
    public int available() {
        return remaining;
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }

    @Override
    public int read() throws IOException {
        if (remaining > 0) {
            remaining--;
            return inputStream.read();
        } else {
            return -1;
        }
//        throw new UnsupportedOperationException("unSupport because of the value byte is returned as an int in the range 0 to 255");
    }
}
