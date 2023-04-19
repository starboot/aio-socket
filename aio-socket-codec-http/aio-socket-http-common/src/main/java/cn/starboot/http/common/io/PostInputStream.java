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
