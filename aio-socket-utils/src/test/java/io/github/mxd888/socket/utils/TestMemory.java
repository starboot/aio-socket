package io.github.mxd888.socket.utils;

import io.github.mxd888.socket.utils.pool.memory.MemoryCell;
import io.github.mxd888.socket.utils.pool.memory.MemoryChunk;
import io.github.mxd888.socket.utils.pool.memory.MemoryPool;
import io.github.mxd888.socket.utils.pool.memory.MemoryPoolFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.Executors;

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

    void testMemory(){
        // 新建内存池工厂，每人2k，十万人申请200M
        long start0 = System.currentTimeMillis();
        MemoryPoolFactory memoryPoolFactory = () -> new MemoryPool(10 * 1024 * 1024 * 2, 10, false);
        MemoryPool memoryPool = memoryPoolFactory.create();
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            //申请一张内存页
            MemoryChunk memoryChunk = memoryPool.allocateBufferPage();

            for (int j = 0; j < 10240; j++) {
                // 申请内存单元
                MemoryCell allocate = memoryChunk.allocate(2 * 1024);
            }

        }
        long end = System.currentTimeMillis();
        System.out.println("从内存池里面申请执行时间：" + (end-start1) + "\r\n" +
                "创建内存池所用时间：" + (start1-start0));
        String s = "hello aio-socket";
        System.out.println(s.getBytes().length + 4);
    }
}
