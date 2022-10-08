package io.github.mxd888.socket.test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class ByteBufferTest {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(20);
        buffer.putInt(100);
        buffer.flip();
        byte[] array = buffer.array();
        System.out.println(Arrays.toString(array));
        byte[] bytes = new byte[8];
        buffer.get(bytes);

        Semaphore semaphore = new Semaphore(10);
        System.out.println(semaphore.availablePermits());


        byte[] bytes1 = new byte[8];
        bytes1[0] = (byte) 5;
        bytes1[1] = (byte) 6;
        bytes1[2] = (byte) 7;

        System.out.println(Arrays.toString(bytes1));


    }
}
