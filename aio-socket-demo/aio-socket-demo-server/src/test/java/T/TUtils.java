package T;

import cn.starboot.socket.Packet;
import cn.starboot.socket.codec.string.StringHandler;
import cn.starboot.socket.codec.string.StringPacket;
import cn.starboot.socket.core.ChannelContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class TUtils {


    public static void main(String[] args) {


        StringHandler stringHandler = new StringHandler() {
            @Override
            public Packet handle(ChannelContext channelContext, StringPacket packet) {
                return null;
            }
        };
        System.out.println(stringHandler.getClass().getGenericInterfaces().length);             // 接口个数 1
        Type genericInterface = stringHandler.getClass().getGenericInterfaces()[0];
        String typeName = genericInterface.getTypeName();
        System.out.println(typeName);                                                   // io.github.mxd888.socket.intf.AioHandler<java.lang.String>
        System.out.println(Arrays.toString(stringHandler.getClass().getGenericInterfaces()));   // 所有接口
        ParameterizedType genericInterface1 = (ParameterizedType) genericInterface;
        Type actualTypeArgument = genericInterface1.getActualTypeArguments()[0];
        System.out.println(actualTypeArgument);                             // class java.lang.String
        Type ownerType = genericInterface1.getOwnerType();
        System.out.println(ownerType);                                      // null
        Type rawType1 = genericInterface1.getRawType();                     // interface io.github.mxd888.socket.intf.AioHandler
        System.out.println(rawType1);
        System.out.println(String.class);
        try {
            String s = (String) Class.forName(actualTypeArgument.getTypeName()).newInstance();
            System.out.println("---" + s);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (actualTypeArgument instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) actualTypeArgument).getRawType();
            System.out.println(rawType);
        } else {
//            actualTypeArgument;
        }
    }
}
