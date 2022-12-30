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
package cn.starboot.http.common.logging;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public final class LoggerFactory {

    private static final Map<String, Logger> loggerMap = new HashMap<>();

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        Logger logger = loggerMap.get(name);
        if (logger != null) {
            return logger;
        }
        logger = new RunLogger(name);
        loggerMap.put(name, logger);
        return logger;
    }
}
