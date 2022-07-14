package plugins;

import io.github.mxd888.socket.core.ServerBootstrap;
import io.github.mxd888.socket.plugins.HeartPlugin;

import java.util.concurrent.TimeUnit;

public class DemoServer {

    public static void main(String[] args) {
        //
        ServerBootstrap bootstrap = new ServerBootstrap("127.0.0.1", 8888, new DemoHandlerClient());

        bootstrap.getConfig().setEnablePlugins(true);
        bootstrap.getConfig().getPlugins().addPlugin(new DemoPlugins());
        bootstrap.getConfig().getPlugins().addPlugin(new HeartPlugin(60, TimeUnit.SECONDS));
        bootstrap.start();
    }
}
