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
package cn.starboot.http.server.decode;

import cn.starboot.http.common.utils.ByteTree;
import cn.starboot.http.common.utils.Constant;
import cn.starboot.http.server.HttpServerConfiguration;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public abstract class AbstractDecoder implements Decoder {
    protected static final ByteTree.EndMatcher CR_END_MATCHER = endByte -> endByte == Constant.CR;
    protected static final ByteTree.EndMatcher SP_END_MATCHER = endByte -> endByte == Constant.SP;
    private final HttpServerConfiguration configuration;

    public AbstractDecoder(HttpServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public HttpServerConfiguration getConfiguration() {
        return configuration;
    }
}