package cn.starboot.socket.test.T;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DefaultTargetType1<T> {

    private Type type;
    private Class<T> classType;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Class<T> getClassType() {
        return classType;
    }

    public void setClassType(Class<T> classType) {
        this.classType = classType;
    }

    @SuppressWarnings("unchecked")
    public DefaultTargetType1() {
        Type superClass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (this.type instanceof ParameterizedType) {
            this.classType = (Class<T>) ((ParameterizedType) this.type).getRawType();
        } else {
            this.classType = (Class<T>) this.type;
        }
    }

    private static class User{

    }
    public static void main(String[] args) {
        Class<User> classType = new DefaultTargetType1<User>() {}.getClassType();
        System.out.println(classType);
    }

}
