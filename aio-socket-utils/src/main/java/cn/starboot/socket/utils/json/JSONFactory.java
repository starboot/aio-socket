package cn.starboot.socket.utils.json;

import cn.starboot.socket.utils.json.serializer.FastJsonSerializer;
import cn.starboot.socket.utils.json.serializer.IJsonSerializer;

/**
 * Created by DELL(mxd) on 2021/12/24 14:02
 */
public class JSONFactory {

    //创建序列化器
    protected static IJsonSerializer createSerializer(){
        return new FastJsonSerializer();
    }
}
