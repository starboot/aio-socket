package io.github.mxd888.socket.utils.scanner;


import java.util.Set;
import java.util.function.Predicate;

/**
 * 包扫描工具包
 */
public class ClassScannerUtils {

    public static Set<Class<?>> searchClasses(String packageName){
        return searchClasses(packageName,null);
    }

    public static Set<Class<?>> searchClasses(String packageName, Predicate<Class<?>> predicate){
        return ScanExecutor.getInstance().search(packageName,predicate);
    }

}
