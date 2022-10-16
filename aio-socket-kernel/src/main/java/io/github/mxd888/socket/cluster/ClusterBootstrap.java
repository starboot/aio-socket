package io.github.mxd888.socket.cluster;

import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.utils.cache.redis.RedisCache;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * 集群服务启动器
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClusterBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterBootstrap.class);

    /**
     * 集群服务器IP+port
     */
    private final String[] hosts;

    /**
     * 配置信息
     */
    private final AioConfig config;

    public ClusterBootstrap(String[] hosts, AioConfig config) {
        this.hosts = hosts;
        this.config = config;
    }

    public void start() throws IOException {
        for (String s: hosts
             ) {
            String[] split = s.split(":");
            System.out.println(Arrays.toString(split));
            ClientBootstrap clientBootstrap = new ClientBootstrap(split[0], Integer.parseInt(split[1]), config.getHandler());
            ChannelContext start = clientBootstrap.start();
//            Packet packet = new Packet();
//            packet.setToId(split[0]);
//            // 写入自己的IP
//            packet.setFromId(this.config.getHost());
//            // 设置内核集群消息标识位
//            packet.setEntity(new ClusterEntity(true));
//            // 向其他服务器注册自己
//            Aio.send(start, packet);
            // 绑定通道ID
            start.setId(split[0]);
            // 绑定到集群服务器组
            this.config.getGroups().join("ClusterServer", start);
            // 将通道与ID对应关系进行绑定
            this.config.getIds().join(start.getId(), start);
        }

    }

    private void init() {
        LOGGER.info("用户选择开启持久化，redis默认连接http://127.0.0.1:6379");
        try {
            Config redisConfig = new Config();
            redisConfig.useSingleServer().setTimeout(1000000).setAddress("redis://127.0.0.1:6379").setDatabase(0);
                /*
                哨兵
                Config config = new Config();
                config.useSentinelServers().addSentinelAddress(
                        "redis://172.29.3.245:1234","redis://172.29.3.245:1234", "redis://172.29.3.245:1234")
                        .setMasterName("mymaster")
                        .setPassword("a123456").setDatabase(0);
                集群
                Config config = new Config();
                config.useClusterServers().addNodeAddress(
                        "redis://172.29.3.245:6375","redis://172.29.3.245:6376", "redis://172.29.3.245:6377",
                        "redis://172.29.3.245:6378","redis://172.29.3.245:6379", "redis://172.29.3.245:6380")
                        .setPassword("a123456").setScanInterval(5000);
                 */
            RedissonClient redissonClient = Redisson.create(redisConfig);
            // timeToLiveSeconds : 生存时间   timeToIdleSeconds 计时器，  设置一个即可
            Long aLong = 604800L;
            // 注册TIMServer
            RedisCache.register(redissonClient, "TIMServer", aLong, null);
            RedisCache.register(redissonClient, "AioSocketCluster", aLong, null);
        } catch (RedisConnectionException redisConnectionException) {
            LOGGER.error("Redis连接失败，持久化失效，请先启动本地Redis服务");
        }
    }
}
