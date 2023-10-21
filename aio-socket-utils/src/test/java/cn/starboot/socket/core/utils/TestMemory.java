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
package cn.starboot.socket.core.utils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 内存池测试类
 * 经测试得出结论：
 * 1、堆外内存申请并不比对内申请慢
 * 2、堆外内存IO效率更好，综上默认零拷贝
 * -----------堆外内存申请----------------
 * 从内存池里面申请执行时间：17
 * 创建内存池所用时间：73
 * -----------堆外内存申请----------------
 * 从内存池里面申请执行时间：19
 * 创建内存池所用时间：158
 */
public class TestMemory {

    public static void main(String[] args) throws IOException {
        TestMemory testMemory = new TestMemory();
        testMemory.testByteBuff();

    }

    void testByteBuff(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        // 1048576-0-1048576-1048576-true-true
        System.out.println(byteBuffer.capacity() + "-" + byteBuffer.position() + "-" +
                byteBuffer.limit() + "-" + byteBuffer.remaining() + "-" +
                byteBuffer.hasRemaining() + "-" + byteBuffer.isDirect());
//        byteBuffer.flip();
        byteBuffer.clear();
        //1048576-0-0-0-false-true
        System.out.println(byteBuffer.capacity() + "-" + byteBuffer.position() + "-" +
                byteBuffer.limit() + "-" + byteBuffer.remaining() + "-" +
                byteBuffer.hasRemaining() + "-" + byteBuffer.isDirect());
    }
}
