package io.github.mxd888.websocket;

/**
 * Created by DELL(mxd) on 2022/8/9 18:27
 */
public class ServerEndpoint {

    private String path = "/ws/{arg}";

    private String host = "0.0.0.0";

    private int port = 80;

    private int bossLoopGroupThreads = 1;

    private int workerLoopGroupThreads = 4;

    private boolean useCompressionHandler = false;

    //------------------------- option -------------------------

    private int optionConnectTimeoutMillis = 30000;

    private int optionSoBacklog = 128;

    //------------------------- childOption -------------------------

    private int childOptionWriteSpinCount = 16;

    private int childOptionWriteBufferHighWaterMark = 65536;

    private int childOptionWriteBufferLowWaterMark = 32768;

    private int childOptionSoRcvbuf = -1;

    private int childOptionSoSndbuf = -1;

    private boolean childOptionTcpNodelay = true;

    private boolean childOptionSoKeepalive = false;

    private int childOptionSoLinger = -1;

    private boolean childOptionAllowHalfClosure = false;

    //------------------------- idleEvent -------------------------

    private int readerIdleTimeSeconds = 0;

    private int writerIdleTimeSeconds = 0;

    private int allIdleTimeSeconds = 0;

    //------------------------- handshake -------------------------

    private int maxFramePayloadLength = 65536;

    //------------------------- eventExecutorGroup -------------------------

    private boolean useEventExecutorGroup = true; //use EventExecutorGroup(another thread pool) to perform time-consuming synchronous business logic

    private int eventExecutorGroupThreads = 16;

    //------------------------- ssl (refer to spring Ssl) -------------------------

    private String sslKeyPassword = "";

    private String sslKeyStore = "";            //e.g. classpath:server.jks

    private String sslKeyStorePassword = "";

    private String sslKeyStoreType = "";        //e.g. JKS

    private String sslTrustStore = "";

    private String sslTrustStorePassword = "";

    private String sslTrustStoreType = "";

    //------------------------- cors (refer to spring CrossOrigin) -------------------------

    private String[] corsOrigins = {};

    private Boolean corsAllowCredentials = false;

    public ServerEndpoint() {
    }

    public ServerEndpoint(String path,
                          String host,
                          int port,
                          int bossLoopGroupThreads,
                          int workerLoopGroupThreads,
                          boolean useCompressionHandler,
                          int optionConnectTimeoutMillis,
                          int optionSoBacklog,
                          int childOptionWriteSpinCount,
                          int childOptionWriteBufferHighWaterMark,
                          int childOptionWriteBufferLowWaterMark,
                          int childOptionSoRcvbuf,
                          int childOptionSoSndbuf,
                          boolean childOptionTcpNodelay,
                          boolean childOptionSoKeepalive,
                          int childOptionSoLinger,
                          boolean childOptionAllowHalfClosure,
                          int readerIdleTimeSeconds,
                          int writerIdleTimeSeconds,
                          int allIdleTimeSeconds,
                          int maxFramePayloadLength,
                          boolean useEventExecutorGroup,
                          int eventExecutorGroupThreads,
                          String sslKeyPassword,
                          String sslKeyStore,
                          String sslKeyStorePassword,
                          String sslKeyStoreType,
                          String sslTrustStore,
                          String sslTrustStorePassword,
                          String sslTrustStoreType,
                          String[] corsOrigins,
                          Boolean corsAllowCredentials) {
        this.path = path;
        this.host = host;
        this.port = port;
        this.bossLoopGroupThreads = bossLoopGroupThreads;
        this.workerLoopGroupThreads = workerLoopGroupThreads;
        this.useCompressionHandler = useCompressionHandler;
        this.optionConnectTimeoutMillis = optionConnectTimeoutMillis;
        this.optionSoBacklog = optionSoBacklog;
        this.childOptionWriteSpinCount = childOptionWriteSpinCount;
        this.childOptionWriteBufferHighWaterMark = childOptionWriteBufferHighWaterMark;
        this.childOptionWriteBufferLowWaterMark = childOptionWriteBufferLowWaterMark;
        this.childOptionSoRcvbuf = childOptionSoRcvbuf;
        this.childOptionSoSndbuf = childOptionSoSndbuf;
        this.childOptionTcpNodelay = childOptionTcpNodelay;
        this.childOptionSoKeepalive = childOptionSoKeepalive;
        this.childOptionSoLinger = childOptionSoLinger;
        this.childOptionAllowHalfClosure = childOptionAllowHalfClosure;
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
        this.allIdleTimeSeconds = allIdleTimeSeconds;
        this.maxFramePayloadLength = maxFramePayloadLength;
        this.useEventExecutorGroup = useEventExecutorGroup;
        this.eventExecutorGroupThreads = eventExecutorGroupThreads;
        this.sslKeyPassword = sslKeyPassword;
        this.sslKeyStore = sslKeyStore;
        this.sslKeyStorePassword = sslKeyStorePassword;
        this.sslKeyStoreType = sslKeyStoreType;
        this.sslTrustStore = sslTrustStore;
        this.sslTrustStorePassword = sslTrustStorePassword;
        this.sslTrustStoreType = sslTrustStoreType;
        this.corsOrigins = corsOrigins;
        this.corsAllowCredentials = corsAllowCredentials;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBossLoopGroupThreads() {
        return bossLoopGroupThreads;
    }

    public void setBossLoopGroupThreads(int bossLoopGroupThreads) {
        this.bossLoopGroupThreads = bossLoopGroupThreads;
    }

    public int getWorkerLoopGroupThreads() {
        return workerLoopGroupThreads;
    }

    public void setWorkerLoopGroupThreads(int workerLoopGroupThreads) {
        this.workerLoopGroupThreads = workerLoopGroupThreads;
    }

    public boolean isUseCompressionHandler() {
        return useCompressionHandler;
    }

    public void setUseCompressionHandler(boolean useCompressionHandler) {
        this.useCompressionHandler = useCompressionHandler;
    }

    public int getOptionConnectTimeoutMillis() {
        return optionConnectTimeoutMillis;
    }

    public void setOptionConnectTimeoutMillis(int optionConnectTimeoutMillis) {
        this.optionConnectTimeoutMillis = optionConnectTimeoutMillis;
    }

    public int getOptionSoBacklog() {
        return optionSoBacklog;
    }

    public void setOptionSoBacklog(int optionSoBacklog) {
        this.optionSoBacklog = optionSoBacklog;
    }

    public int getChildOptionWriteSpinCount() {
        return childOptionWriteSpinCount;
    }

    public void setChildOptionWriteSpinCount(int childOptionWriteSpinCount) {
        this.childOptionWriteSpinCount = childOptionWriteSpinCount;
    }

    public int getChildOptionWriteBufferHighWaterMark() {
        return childOptionWriteBufferHighWaterMark;
    }

    public void setChildOptionWriteBufferHighWaterMark(int childOptionWriteBufferHighWaterMark) {
        this.childOptionWriteBufferHighWaterMark = childOptionWriteBufferHighWaterMark;
    }

    public int getChildOptionWriteBufferLowWaterMark() {
        return childOptionWriteBufferLowWaterMark;
    }

    public void setChildOptionWriteBufferLowWaterMark(int childOptionWriteBufferLowWaterMark) {
        this.childOptionWriteBufferLowWaterMark = childOptionWriteBufferLowWaterMark;
    }

    public int getChildOptionSoRcvbuf() {
        return childOptionSoRcvbuf;
    }

    public void setChildOptionSoRcvbuf(int childOptionSoRcvbuf) {
        this.childOptionSoRcvbuf = childOptionSoRcvbuf;
    }

    public int getChildOptionSoSndbuf() {
        return childOptionSoSndbuf;
    }

    public void setChildOptionSoSndbuf(int childOptionSoSndbuf) {
        this.childOptionSoSndbuf = childOptionSoSndbuf;
    }

    public boolean isChildOptionTcpNodelay() {
        return childOptionTcpNodelay;
    }

    public void setChildOptionTcpNodelay(boolean childOptionTcpNodelay) {
        this.childOptionTcpNodelay = childOptionTcpNodelay;
    }

    public boolean isChildOptionSoKeepalive() {
        return childOptionSoKeepalive;
    }

    public void setChildOptionSoKeepalive(boolean childOptionSoKeepalive) {
        this.childOptionSoKeepalive = childOptionSoKeepalive;
    }

    public int getChildOptionSoLinger() {
        return childOptionSoLinger;
    }

    public void setChildOptionSoLinger(int childOptionSoLinger) {
        this.childOptionSoLinger = childOptionSoLinger;
    }

    public boolean isChildOptionAllowHalfClosure() {
        return childOptionAllowHalfClosure;
    }

    public void setChildOptionAllowHalfClosure(boolean childOptionAllowHalfClosure) {
        this.childOptionAllowHalfClosure = childOptionAllowHalfClosure;
    }

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public int getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public void setAllIdleTimeSeconds(int allIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    public int getMaxFramePayloadLength() {
        return maxFramePayloadLength;
    }

    public void setMaxFramePayloadLength(int maxFramePayloadLength) {
        this.maxFramePayloadLength = maxFramePayloadLength;
    }

    public boolean isUseEventExecutorGroup() {
        return useEventExecutorGroup;
    }

    public void setUseEventExecutorGroup(boolean useEventExecutorGroup) {
        this.useEventExecutorGroup = useEventExecutorGroup;
    }

    public int getEventExecutorGroupThreads() {
        return eventExecutorGroupThreads;
    }

    public void setEventExecutorGroupThreads(int eventExecutorGroupThreads) {
        this.eventExecutorGroupThreads = eventExecutorGroupThreads;
    }

    public String getSslKeyPassword() {
        return sslKeyPassword;
    }

    public void setSslKeyPassword(String sslKeyPassword) {
        this.sslKeyPassword = sslKeyPassword;
    }

    public String getSslKeyStore() {
        return sslKeyStore;
    }

    public void setSslKeyStore(String sslKeyStore) {
        this.sslKeyStore = sslKeyStore;
    }

    public String getSslKeyStorePassword() {
        return sslKeyStorePassword;
    }

    public void setSslKeyStorePassword(String sslKeyStorePassword) {
        this.sslKeyStorePassword = sslKeyStorePassword;
    }

    public String getSslKeyStoreType() {
        return sslKeyStoreType;
    }

    public void setSslKeyStoreType(String sslKeyStoreType) {
        this.sslKeyStoreType = sslKeyStoreType;
    }

    public String getSslTrustStore() {
        return sslTrustStore;
    }

    public void setSslTrustStore(String sslTrustStore) {
        this.sslTrustStore = sslTrustStore;
    }

    public String getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public void setSslTrustStorePassword(String sslTrustStorePassword) {
        this.sslTrustStorePassword = sslTrustStorePassword;
    }

    public String getSslTrustStoreType() {
        return sslTrustStoreType;
    }

    public void setSslTrustStoreType(String sslTrustStoreType) {
        this.sslTrustStoreType = sslTrustStoreType;
    }

    public String[] getCorsOrigins() {
        return corsOrigins;
    }

    public void setCorsOrigins(String[] corsOrigins) {
        this.corsOrigins = corsOrigins;
    }

    public Boolean getCorsAllowCredentials() {
        return corsAllowCredentials;
    }

    public void setCorsAllowCredentials(Boolean corsAllowCredentials) {
        this.corsAllowCredentials = corsAllowCredentials;
    }
}
