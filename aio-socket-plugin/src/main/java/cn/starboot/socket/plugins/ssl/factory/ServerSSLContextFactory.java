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
package cn.starboot.socket.plugins.ssl.factory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

public class ServerSSLContextFactory implements SSLContextFactory {
    private final InputStream keyStoreInputStream;
    private final String keyStorePassword;
    private final String keyPassword;

    public ServerSSLContextFactory(InputStream keyStoreInputStream, String keyStorePassword, String keyPassword) {
        this.keyStoreInputStream = keyStoreInputStream;
        this.keyStorePassword = keyStorePassword;
        this.keyPassword = keyPassword;
    }

    @Override
    public SSLContext create() throws Exception {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(keyStoreInputStream, keyStorePassword.toCharArray());
        kmf.init(ks, keyPassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, null, new SecureRandom());
        return sslContext;
    }
}
