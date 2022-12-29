package io.github.mxd888.http.common;

import io.github.mxd888.http.common.enums.HeaderNameEnum;
import io.github.mxd888.http.common.utils.Constant;
import io.github.mxd888.http.common.utils.GzipUtils;
import cn.starboot.socket.core.ChannelContext;
import cn.starboot.socket.core.WriteBuffer;

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

    protected final ChannelContext channelContext;
    protected final WriteBuffer writeBuffer;




    public ChannelContext getChannelContext() {
        return channelContext;
    }

    public BufferOutputStream(ChannelContext channelContext) {
        this.channelContext = channelContext;
        this.writeBuffer = channelContext.getWriteBuffer();
        this.channelContext.attr("BufferOutputStream", this);
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
