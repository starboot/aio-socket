package io.github.mxd888.http.common;

import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.GzipUtils;
import io.github.mxd888.socket.utils.pool.buffer.VirtualBuffer;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.TCPChannelContext;

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
    protected final TCPChannelContext channelContext;
    protected VirtualBuffer virtualBuffer = null;
    protected boolean committed = false;
    protected boolean chunked = false;
    protected boolean gzip = false;

    /**
     * 当前流是否完结
     */
    private boolean closed = false;

    public BufferOutputStream(TCPChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    @Override
    public final void write(int b) {
        throw new UnsupportedOperationException();
    }

    /**
     * 输出Http响应
     *
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    public final void write(byte[] b, int off, int len) throws IOException {
        check();
        writeHeader();
        if (chunked) {
            if (gzip) {
                b = GzipUtils.compress(b, off, len);
                off = 0;
                len = b.length;
            }
            byte[] start = getBytes(Integer.toHexString(len) + "\r\n");
            virtualBuffer.buffer().put(start);
            virtualBuffer.buffer().put(b, off, len);
            virtualBuffer.buffer().put(Constant.CRLF_BYTES);
        } else {
            if (virtualBuffer.buffer().remaining() == 0) {
                virtualBuffer = channelContext.getVirtualBuffer();
            }
            virtualBuffer.buffer().put(b, off, len);
        }
        flush();
    }

    /**
     * 直接输出，不执行编码
     *
     * @param b
     * @param off
     * @param len
     */
    public final void directWrite(byte[] b, int off, int len) throws IOException {
//        virtualBuffer = channelContext.getByteBuf();
        virtualBuffer.buffer().put(b, off, len);
    }

    public final void write(ByteBuffer buffer) throws IOException {
        check();
        System.out.println("write(ByteBuffer buffer)");
        writeHeader();
//        virtualBuffer = channelContext.getByteBuf();
        if (chunked) {
            byte[] start = getBytes(Integer.toHexString(buffer.remaining()) + "\r\n");
            virtualBuffer.buffer().put(start);
            virtualBuffer.buffer().put(buffer);
            virtualBuffer.buffer().put(Constant.CRLF_BYTES);
        } else {
            virtualBuffer.buffer().put(buffer);
        }
    }

    @Override
    public final void flush() throws IOException {
        writeHeader();
        virtualBuffer.buffer().flip();
        Aio.send(channelContext, virtualBuffer);
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            throw new IOException("outputStream has already closed");
        }
        writeHeader();

        if (chunked) {
            virtualBuffer = channelContext.getVirtualBuffer();
            virtualBuffer.buffer().put(Constant.CHUNKED_END_BYTES);
        }
        closed = true;
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

    protected static class WriteCache {
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
