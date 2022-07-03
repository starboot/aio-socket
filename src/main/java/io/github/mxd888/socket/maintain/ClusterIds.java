package io.github.mxd888.socket.maintain;

import java.util.HashMap;
import java.util.Map;

/**
 * 集群消息业务逻辑类
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClusterIds {

    /**
     * 将用户ID与所在集群服务器ID进行映射、存储到Map中
     */
    private final Map<String, String> users = new HashMap<>();

    /**
     * 绑定用户与集群服务器ID的关系
     *
     * @param userId   用户ID
     * @param serverId 用户所在机器编号
     */
    public final synchronized void join(String userId, String serverId) {
        users.put(userId, serverId);
    }

    /**
     * 从用户集群服务器表中删除某个用户
     *
     * @param userId 用户ID
     */
    public final void remove(String userId) {
        String context = users.get(userId);
        if (context == null) {
            return;
        }
        users.remove(userId);
    }

    /**
     * 根据用户ID获取所在集群服务器的ID
     *
     * @param userId 用户ID
     * @return       服务器ID
     */
    public final String get(String userId) {
        return users.get(userId);
    }
}
