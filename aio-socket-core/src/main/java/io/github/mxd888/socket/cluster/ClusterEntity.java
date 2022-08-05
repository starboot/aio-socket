package io.github.mxd888.socket.cluster;

/**
 * 集群化消息包
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class ClusterEntity {

    /**
     * 是否用来认证，true：绑定通道与服务的对应关系， false：绑定用户所在集群服务器的关系
     */
    private boolean isAuth;

    public ClusterEntity() {
    }

    public ClusterEntity(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }
}
