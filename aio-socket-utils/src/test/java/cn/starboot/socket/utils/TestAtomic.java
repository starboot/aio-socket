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
package cn.starboot.socket.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class TestAtomic {

    private static final AtomicInteger takeIndex = new AtomicInteger(-1);

    private final int i = 0;

    public static void main(String[] args) {

        // takeIndex.intValue()  == takeIndex.get()
        System.out.println(takeIndex.intValue());
        System.out.println(takeIndex.getAndIncrement());
        System.out.println(takeIndex.get());
        System.out.println(takeIndex.incrementAndGet());
        System.out.println(takeIndex.get());
        takeIndex.set(0);
        System.out.println(takeIndex.get());
        takeIndex.lazySet(6);
        System.out.println(takeIndex.getAndSet(8));
        System.out.println(takeIndex.compareAndSet(8, 3));
        System.out.println(takeIndex.getAndIncrement());
        System.out.println(takeIndex.get());

        TestAtomic atomic = null;

        if (atomic != null && (8 / atomic.i) > 1) {
            System.out.println("执行完毕");
        }else {
            System.out.println("不执行");
        }

    }
}
