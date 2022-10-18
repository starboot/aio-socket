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
package io.github.mxd888.socket.test.myEnum;

import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 生成枚举工具类
 **/
public class GenerateEnumUtils {

    private static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();

    /**
     * 添加枚举类
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称 (不可重复)
     * @param params    属性参数，按顺序写入
     */
    public static <T extends Enum<?>> void addEnum(Class<T> enumClass, String enumName, Object... params) {
        sanityChecks(enumClass, enumName);
        // 获取枚举合成方法和枚举属性类型
        Field valuesField = null;
        Field[] fields = enumClass.getDeclaredFields();
        List<Class<?>> paramTypes = new LinkedList<>();
        for (Field field : fields) {
            if (field.isEnumConstant() && field.getName().equals(enumName)) {
//                log.warn("该枚举类已经存在!");
                return;
            }
            if (field.isSynthetic()) {
                valuesField = field;
            }
            if (!field.isSynthetic() && !field.isEnumConstant()) {
                paramTypes.add(field.getType());
            }
        }
        if (valuesField == null) {
            throw new RuntimeException("未获取到合成类型");
        }

        // 设置属性访问权限
        AccessibleObject.setAccessible(new Field[]{valuesField}, true);
        try {
            // 复制一份
            T[] previousValues = (T[]) valuesField.get(enumClass);
            List<T> values = new ArrayList<T>(Arrays.asList(previousValues));
            // 创建一个新的枚举
            T newValue = (T) makeEnum(enumClass, enumName, values.size(), paramTypes.toArray(new Class[paramTypes.size()]), params);
            // 添加一个新的枚举
            values.add(newValue);
            // 设置新值字段
            setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumClass, 0)));
            // 清理枚举缓存
            cleanEnumCache(enumClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 校验参数
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     */
    private static <T extends Enum<?>> void sanityChecks(Class<T> enumClass, String enumName) {
        // 判断是否枚举类
        if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new RuntimeException(enumClass + " 不是一个枚举类。");
        }
        if (enumName == null || enumName.trim().length() <= 0) {
            throw new RuntimeException("枚举名称不能为空");
        }
    }

    /**
     * @param enumClass        枚举类
     * @param enumName         枚举名称
     * @param ordinal          添加到枚举类位置
     * @param additionalTypes  属性类型
     * @param additionalValues 属性值
     * @return java.lang.Object
     */
    private static Object makeEnum(Class<?> enumClass, String enumName, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {
        Object[] params = new Object[additionalValues.length + 2];
        params[0] = enumName;
        params[1] = ordinal;
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
        return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(params));
    }

    /**
     * 获取构造器
     *
     * @param enumClass                枚举类
     * @param additionalParameterTypes 附加参数类型
     * @return sun.reflect.ConstructorAccessor
     * @throws NoSuchMethodException 未获取到
     */
    private static ConstructorAccessor getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(additionalParameterTypes, 0,
                parameterTypes, 2, additionalParameterTypes.length);
        return REFLECTION_FACTORY.newConstructorAccessor(enumClass.getDeclaredConstructor(parameterTypes));
    }

    /**
     * 设置新值字段
     *
     * @param field  属性对象
     * @param target 目标对象
     * @param value  值
     */
    private static void setFailsafeFieldValue(Field field, Object target, Object value) throws NoSuchFieldException, IllegalAccessException {
        // 设置可访问
        field.setAccessible(true);
        // 修改 final 属性可访问
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(field);
        // 清空修饰符 int 中的最后一位
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(field, modifiers);
        FieldAccessor fa = REFLECTION_FACTORY.newFieldAccessor(field, false);
        fa.set(target, value);
    }

    private static void cleanEnumCache(Class<?> enumClass)
            throws NoSuchFieldException, IllegalAccessException {
        // Sun (Oracle?!?) JDK 1.5/6
        blankField(enumClass, "enumConstantDirectory");
        // IBM JDK
        blankField(enumClass, "enumConstants");
    }

    private static void blankField(Class<?> enumClass, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        for (Field field : Class.class.getDeclaredFields()) {
            if (field.getName().contains(fieldName)) {
                AccessibleObject.setAccessible(new Field[]{field}, true);
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }
    }



    public static void main(String[] args) {
        System.out.println(Arrays.toString(TestEnum.values()));
        addEnum(TestEnum.class, "TEST_ENUM", "TEST_ENUM");
        addEnum(TestEnum.class, "DEFAULT_ENUM", "TEST_ENUM2");
        System.out.println(Arrays.toString(TestEnum.values()));
    }
}

enum TestEnum {
    DEFAULT_ENUM("DEFAULT_ENUM");
    private final String code;

    TestEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
