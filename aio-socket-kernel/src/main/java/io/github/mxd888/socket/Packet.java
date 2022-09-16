package io.github.mxd888.socket;

import io.github.mxd888.socket.cluster.ClusterEntity;

import java.io.Serializable;

/**
 * TCP报文数据包
 * 本packet只提供报文发送所必需的的参数，其他根据自己业务进行拓展
 * <p>编写私有化协议的时候一定要把这里的所有参数进行非空编码(即 if(entity != null) 将entity放入ByteBuffer内)</p>
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Packet implements Serializable {

    private static final long serialVersionUID = -4108736058242170393L;

    /**
     * TCP报文版本号
     */
    private byte versionID;

    /**
     * 同步消息唯一ID
     */
    private String synNum;

    /**
     * 发送者ID
     */
    private String fromId;

    /**
     * 接受者ID
     */
    private String toId;

    /**
     * 内核集群所需实体
     */
    private ClusterEntity entity;

    public byte getVersionID() {
        return versionID;
    }

    public void setVersionID(byte versionID) {
        this.versionID = versionID;
    }

    public String getSynNum() {
        return synNum;
    }

    public void setSynNum(String synNum) {
        this.synNum = synNum;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public ClusterEntity getEntity() {
        return entity;
    }

    public void setEntity(ClusterEntity entity) {
        this.entity = entity;
    }
}
