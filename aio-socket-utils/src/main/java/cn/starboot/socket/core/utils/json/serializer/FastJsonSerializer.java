package cn.starboot.socket.core.utils.json.serializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by DELL(mxd) on 2021/12/24 14:03
 */
public class FastJsonSerializer implements IJsonSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonSerializer.class);

	@Override
    public <T> String toString(T t){
        if(t==null){
            return null;
        }
        //加上WriteMapNullValue 使得null值也被序列化
        return JSON.toJSONString(t, JSONWriter.Feature.WriteMapNullValue);
    }

	@Override
    public <T> T toObject(String json,Class<T> clazz){
        T t = null;
        try {
            t = JSON.parseObject(json,clazz);
        }catch (Exception e){
			throw new RuntimeException(e);
        }
        return t;
    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> clazz) {
        try {
            if (bytes == null) {
                return null;
            }

            return JSON.parseObject(bytes, clazz);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

	@Override
    public <T> List<T> toArray(String json, Class<T> clazz){
        try {
            return JSON.parseArray(json, clazz);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> byte[] toByte(T t) {
        return toString(t).getBytes();
    }
}
