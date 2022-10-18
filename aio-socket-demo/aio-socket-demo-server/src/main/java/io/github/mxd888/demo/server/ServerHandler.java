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
package io.github.mxd888.demo.server;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.codec.string.StringHandler;
import io.github.mxd888.socket.codec.string.StringPacket;
import io.github.mxd888.socket.core.ChannelContext;

import java.nio.charset.Charset;

public class ServerHandler extends StringHandler {

    @Override
    public Packet handle(ChannelContext channelContext, StringPacket packet) {
        return packet;
    }
}
