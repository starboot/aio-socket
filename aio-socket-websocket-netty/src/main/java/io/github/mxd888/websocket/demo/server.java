package io.github.mxd888.websocket.demo;

import io.github.mxd888.websocket.ServerEndpoint;
import io.github.mxd888.websocket.pojo.PojoEndpointServer;
import io.github.mxd888.websocket.standard.ServerEndpointConfig;
import io.github.mxd888.websocket.standard.WebSocketServer;

import javax.net.ssl.SSLException;

/**
 * Created by DELL(mxd) on 2022/8/10 12:38
 */
public class server {

    public static void main(String[] args) throws SSLException {

        myWs myWs = new myWs();
        ServerEndpoint serverEndpoint = new ServerEndpoint();
        ServerEndpointConfig serverEndpointConfig = new ServerEndpointConfig(serverEndpoint.getHost(),
                serverEndpoint.getPort(),
                serverEndpoint.getBossLoopGroupThreads(),
                serverEndpoint.getWorkerLoopGroupThreads(),
                serverEndpoint.isUseCompressionHandler(),
                serverEndpoint.getOptionConnectTimeoutMillis(),
                serverEndpoint.getOptionSoBacklog(),
                serverEndpoint.getChildOptionWriteSpinCount(),
                serverEndpoint.getChildOptionWriteBufferHighWaterMark(),
                serverEndpoint.getChildOptionWriteBufferLowWaterMark(),
                serverEndpoint.getChildOptionSoRcvbuf(),
                serverEndpoint.getChildOptionSoSndbuf(),
                serverEndpoint.isChildOptionTcpNodelay(),
                serverEndpoint.isChildOptionSoKeepalive(),
                serverEndpoint.getChildOptionSoLinger(),
                serverEndpoint.isChildOptionAllowHalfClosure(),
                serverEndpoint.getReaderIdleTimeSeconds(),
                serverEndpoint.getWriterIdleTimeSeconds(),
                serverEndpoint.getAllIdleTimeSeconds(),
                serverEndpoint.getMaxFramePayloadLength(),
                serverEndpoint.isUseEventExecutorGroup(),
                serverEndpoint.getEventExecutorGroupThreads(),
                serverEndpoint.getSslKeyPassword(),
                serverEndpoint.getSslKeyStore(),
                serverEndpoint.getSslKeyStorePassword(),
                serverEndpoint.getSslKeyStoreType(),
                serverEndpoint.getSslTrustStore(),
                serverEndpoint.getSslTrustStorePassword(),
                serverEndpoint.getSslTrustStoreType(),
                serverEndpoint.getCorsOrigins(),
                serverEndpoint.getCorsAllowCredentials());
        PojoEndpointServer pojoEndpointServer = new PojoEndpointServer(myWs, serverEndpointConfig, serverEndpoint.getPath());
        WebSocketServer webSocketServer = new WebSocketServer(pojoEndpointServer, serverEndpointConfig);
        webSocketServer.init();


    }
}
