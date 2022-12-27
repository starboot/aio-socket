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
package cn.starboot.socket.utils.scanner;

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
