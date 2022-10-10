package io.github.mxd888.socket.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class TestAtomic {

    private static final AtomicInteger takeIndex = new AtomicInteger(-1);

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

    }
}
