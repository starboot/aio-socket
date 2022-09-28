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
package io.github.mxd888.socket.utils.pool.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    /** The cacheMap of name and thread factory. */
    private static final Map<String, DefaultThreadFactory> mapOfNameAndThreadFactory = new HashMap<>();

    /** The cacheMap of name and atomic integer. */
    private static final Map<String, AtomicInteger> mapOfNameAndAtomicInteger = new HashMap<>();

    public static DefaultThreadFactory getInstance(String threadName) {
        return getInstance(threadName, Thread.NORM_PRIORITY);
    }

    /**
     * Gets the single INSTANCE of DefaultThreadFactory.
     *
     * @param threadName the thread name
     * @param priority the priority
     * @return single INSTANCE of DefaultThreadFactory
     */
    public static DefaultThreadFactory getInstance(String threadName, Integer priority) {
        DefaultThreadFactory defaultThreadFactory = mapOfNameAndThreadFactory.get(threadName);
        if (defaultThreadFactory == null) {
            defaultThreadFactory = new DefaultThreadFactory();
            if (priority != null) {
                defaultThreadFactory.priority = priority;
            }

            defaultThreadFactory.setThreadName(threadName);
            mapOfNameAndThreadFactory.put(threadName, defaultThreadFactory);
            mapOfNameAndAtomicInteger.put(threadName, new AtomicInteger());
        }
        return defaultThreadFactory;
    }

    /** The thread pool name. */
    private String threadPoolName = null;

    /** The priority. */
    private int priority = Thread.NORM_PRIORITY;

    /**
     * Instantiates a new default thread factory.
     */
    private DefaultThreadFactory() {

    }

    /**
     * Gets the thread pool name.
     *
     * @return the thread pool name
     */
    public String getThreadPoolName() {
        return threadPoolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(this.getThreadPoolName() + "-" + mapOfNameAndAtomicInteger.get(this.getThreadPoolName()).incrementAndGet());
        thread.setPriority(priority);
        return thread;
    }

    /**
     * Sets the thread name.
     *
     * @param threadName the new thread name
     */
    public void setThreadName(String threadName) {
        this.threadPoolName = threadName;
    }

}