package io.github.mxd888.socket.plugins;

import io.github.mxd888.socket.core.AioConfig;

/**
 * Created by DELL(mxd) on 2022/7/28 17:48
 */
public class ACKPlugin extends AbstractPlugin{

    public ACKPlugin() {
        System.out.println("aio-socket "+"version: " + AioConfig.VERSION + "; server kernel's ACK plugin added successfully");
    }
}
