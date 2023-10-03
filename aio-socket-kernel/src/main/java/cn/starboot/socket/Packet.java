/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.starboot.socket;

import cn.starboot.socket.utils.json.JsonUtil;
import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * TCP报文数据包
 * 本packet只提供报文发送所必需的的参数，其他根据自己业务进行拓展
 * <p>编写私有化协议的时候一定要把这里的所有参数进行非空编码(即 if(entity != null) 将entity放入ByteBuffer内)</p>
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class Packet implements Serializable {

	/* uid */
	private static final long serialVersionUID = -1862775872908377468L;

	/**
     * TCP报文版本号
     */
    protected byte versionID;

    /**
     * 同步消息唯一ID
     */
	protected Integer req;

    /**
     * 同步消息响应ID
     */
	protected Integer resp;

    /**
     * 发送者ID
     */
	protected String fromId;

    /**
     * 接受者ID
     */
	protected String toId;

	/**
	 * 最后一次发送时间
	 */
	protected long latestTime;

	/**
	 * 扩展参数字段
	 */
	protected JSONObject extras;

	public Packet() {
	}

	private Packet(byte versionID, Integer req, Integer resp, String fromId, String toId, long latestTime, JSONObject extras) {
		this.versionID = versionID;
		this.req = req;
		this.resp = resp;
		this.fromId = fromId;
		this.toId = toId;
		this.latestTime = latestTime;
		this.extras = extras;
	}

	public byte getVersionID() {
        return versionID;
    }

    public void setVersionID(byte versionID) {
        this.versionID = versionID;
    }

    public Integer getReq() {
        return req;
    }

    public void setReq(Integer req) {
        this.req = req;
    }

    public Integer getResp() {
        return resp;
    }

    public void setResp(Integer resp) {
        this.resp = resp;
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

	public long getLatestTime() {
		return latestTime;
	}

	public void setLatestTime(long latestTime) {
		this.latestTime = latestTime;
	}

	public JSONObject getExtras() {
		return extras;
	}

	public void setExtras(JSONObject extras) {
		this.extras = extras;
	}

	public String toJsonString() {
		return JsonUtil.toJSONString(this);
	}

	public abstract static class Builder<T extends Packet, B extends Builder<T, B>> {

		/**
		 * TCP报文版本号
		 */
		protected byte versionID;

		/**
		 * 同步消息唯一ID
		 */
		protected Integer req;

		/**
		 * 同步消息响应ID
		 */
		protected Integer resp;

		/**
		 * 发送者ID
		 */
		protected String fromId;

		/**
		 * 接受者ID
		 */
		protected String toId;

		protected long latestTime;

		/**
		 * 扩展参数字段
		 */
		protected JSONObject extras;

		private final B theBuilder = this.getThis();

		protected abstract B getThis();

		public B setVersionID(byte versionID) {
			this.versionID = versionID;
			return this.theBuilder;
		}

		public B setReq(Integer req) {
			this.req = req;
			return this.theBuilder;
		}

		public B setResp(Integer resp) {
			this.resp = resp;
			return this.theBuilder;
		}

		public B setFromId(String fromId) {
			this.fromId = fromId;
			return this.theBuilder;
		}

		public B setToId(String toId) {
			this.toId = toId;
			return this.theBuilder;
		}

		public B setToLatestTime(long latestTime) {
			this.latestTime = latestTime;
			return this.theBuilder;
		}

		public B addExtra(String key, Object value) {
			if (Objects.nonNull(key) && key.length() > 0 && Objects.nonNull(value)) {
				if (Objects.isNull(this.extras)) {
					this.extras = new JSONObject();
				}
				this.extras.put(key, value);
			}
			return this.theBuilder;
		}

		public abstract T build();

		@Override
		public String toString() {
			return "Packet.Builder{" +
					"versionID=" + versionID +
					", req='" + req + '\'' +
					", resp='" + resp + '\'' +
					", fromId='" + fromId + '\'' +
					", toId='" + toId + '\'' +
					", latestTime='" + latestTime + '\'' +
					", extras=" + extras +
					", theBuilder=" + theBuilder +
					'}';
		}
	}

	@Override
	public String toString() {
		return toJsonString();
	}
}
