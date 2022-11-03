package io.github.mxd888.http.common;

import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.GzipUtils;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class BufferOutputStream extends OutputStream implements Reset {
    private static final Map<String, byte[]> HEADER_NAME_EXT_MAP = new ConcurrentHashMap<>();
    protected final ChannelContext channelContext;
    protected final WriteBuffer writeBuffer;
    protected boolean committed = false;
    protected boolean chunked = false;
    protected boolean gzip = false;

    /**
     * 当前流是否完结
     */
    private boolean closed = false;

    public ChannelContext getChannelContext() {
        return channelContext;
    }

    public BufferOutputStream(ChannelContext channelContext) {
        this.channelContext = channelContext;
        this.writeBuffer = channelContext.getWriteBuffer();
        this.channelContext.attr("BufferOutputStream", this);
    }

    @Override
    public final void write(int b) {
        throw new UnsupportedOperationException();
    }

    public final void write(byte[] b, int off, int len) throws IOException {
        check();
        writeHeader();
        if (chunked) {
            System.out.println("chunked:" + chunked);
            if (gzip) {
                b = GzipUtils.compress(b, off, len);
                off = 0;
                len = b.length;
            }
            byte[] start = getBytes(Integer.toHexString(len) + "\r\n");
            writeBuffer.write(start);
            writeBuffer.write(b, off, len);
            writeBuffer.write(Constant.CRLF_BYTES);
        } else {
            System.out.println("不chunked");
            writeBuffer.write(b, off, len);
        }
    }

    public final void directWrite(byte[] b, int off, int len) throws IOException {
        writeBuffer.write(b, off, len);
    }

    public final void write(ByteBuffer buffer) throws IOException {
        check();
        System.out.println("write(ByteBuffer buffer)");
        writeHeader();
        if (chunked) {
            byte[] start = getBytes(Integer.toHexString(buffer.remaining()) + "\r\n");
            writeBuffer.write(start);
            writeBuffer.write(buffer.array());
            writeBuffer.write(Constant.CRLF_BYTES);
        } else {
            writeBuffer.write(buffer.array());
        }
    }

    @Override
    public final void flush() throws IOException {
        writeHeader();
        System.out.println("直接输出");
        writeBuffer.flush();
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("outputStream has already closed");
        }
        writeHeader();

        if (chunked) {
            writeBuffer.write(Constant.CHUNKED_END_BYTES);
        }
        closed = true;
        // 在这里输出一下
    }

    protected final byte[] getHeaderNameBytes(String name) {
        HeaderNameEnum headerNameEnum = HeaderNameEnum.HEADER_NAME_ENUM_MAP.get(name);
        if (headerNameEnum != null) {
            return headerNameEnum.getBytesWithColon();
        }
        byte[] extBytes = HEADER_NAME_EXT_MAP.get(name);
        if (extBytes == null) {
            synchronized (name) {
                extBytes = getBytes(name + ":");
                HEADER_NAME_EXT_MAP.put(name, extBytes);
            }
        }
        return extBytes;
    }

    protected final byte[] getBytes(String str) {
        return str.getBytes(StandardCharsets.US_ASCII);
    }

    public final boolean isClosed() {
        return closed;
    }

    public final void reset() {
        committed = closed = chunked = gzip = false;
    }


    protected abstract void writeHeader() throws IOException;

    protected abstract void check();

	public static class WriteCache {
        private final byte[] cacheData;
        private final Semaphore semaphore = new Semaphore(1);
        private long expireTime;


        public WriteCache(long cacheTime, byte[] data) {
            this.expireTime = cacheTime;
            this.cacheData = data;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public Semaphore getSemaphore() {
            return semaphore;
        }

        public byte[] getCacheData() {
            return cacheData;
        }

    }
}
