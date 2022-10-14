package io.github.mxd888.socket.utils.scanner;

import java.util.Set;

/**
 * 测试包扫描工具包
 */
public class TestClassScannerUtils {

    public static void main(String[] args) {
        Set<Class<?>> classes = ClassScannerUtils.searchClasses("io.github.mxd888.socket.utils.scanner");
        classes.forEach(System.out::println);

        System.out.println("----------------------------------------------");
        Set<Class<?>> classes1 = ClassScannerUtils.searchClasses("io.github.mxd888.socket.utils.scanner", aClass -> aClass == JarScanner.class);
        classes1.forEach(System.out::println);
        classes1.forEach(aClass -> {
            try {
                System.out.println("Class路径：" + aClass.getName());

                JarScanner jarScanner = (JarScanner) aClass.newInstance();
                jarScanner.testS("我找到你啦");
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }
}
