package io.github.mxd888.socket.utils;

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
