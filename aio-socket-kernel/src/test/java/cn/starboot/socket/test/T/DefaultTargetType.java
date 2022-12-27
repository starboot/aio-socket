package cn.starboot.socket.test.T;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class DefaultTargetType<T> implements MyType<String> {

    private Type type;
    private Class<T> classType;

    public static void main(String[] args) {
        Class<List<String>> classType = new DefaultTargetType<List<String>>() {}.getClassType();
    }

    @SuppressWarnings("unchecked")
    public DefaultTargetType() {
        System.out.println(getClass().getGenericInterfaces().length);
        Type superClass = getClass().getGenericInterfaces()[0];
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (this.type instanceof ParameterizedType) {
            this.classType = (Class<T>) ((ParameterizedType) this.type).getRawType();
        } else {
            this.classType = (Class<T>) this.type;
        }
    }

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
}
