package io.github.mxd888.socket.plugins.ssl.factory;

import javax.net.ssl.SSLContext;

/**
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public interface SSLContextFactory {
    SSLContext create() throws Exception;
}
