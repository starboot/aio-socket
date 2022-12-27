package cn.starboot.socket.test.T;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        Class<List<String>> classType = new DefaultTargetType<List<String>>() {}.getClassType();
    }
}
