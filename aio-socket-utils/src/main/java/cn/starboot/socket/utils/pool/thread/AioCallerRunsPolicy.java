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
package cn.starboot.socket.utils.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public class AioCallerRunsPolicy extends CallerRunsPolicy {

    private static final Logger log = LoggerFactory.getLogger(AioCallerRunsPolicy.class);

    public AioCallerRunsPolicy() {
    }

    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        log.error(r.getClass().getSimpleName());
        super.rejectedExecution(r, e);
    }

}
