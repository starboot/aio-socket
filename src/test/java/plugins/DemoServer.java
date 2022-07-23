package plugins;

import io.github.mxd888.socket.core.ServerBootstrap;
import io.github.mxd888.socket.plugins.HeartPlugin;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DemoServer {

    public static void main(String[] args) {
        //
//        ServerBootstrap bootstrap = new ServerBootstrap("127.0.0.1", 8888, new DemoHandlerClient());
//
//        bootstrap.getConfig().setEnablePlugins(true);
//        bootstrap.getConfig().getPlugins().addPlugin(new DemoPlugins());
//        bootstrap.getConfig().getPlugins().addPlugin(new HeartPlugin(60, TimeUnit.SECONDS));
//        bootstrap.start();

        String s = "15511090450";
        System.out.println(Arrays.toString(s.getBytes()));

        System.out.println(Integer.BYTES);


        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.putInt(s.length());
        buffer.put(s.getBytes());
        buffer.putInt(s.length());
        buffer.put(s.getBytes());
        buffer.putInt(2);
        buffer.flip();
        System.out.println(buffer.remaining());


    }
}
