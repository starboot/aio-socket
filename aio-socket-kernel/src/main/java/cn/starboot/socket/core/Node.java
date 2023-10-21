package cn.starboot.socket.core;

/**
 * 客户端节点
 */
public class Node {

	private String ip;

	private int port;

	private Node(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static class Builder {

		private String ip;

		private int port;

		private Builder() {
		}

		public Builder setIp(String ip) {
			this.ip = ip;
			return this;
		}

		public Builder setPort(int port) {
			this.port = port;
			return this;
		}

		protected Builder getThis() {
			return this;
		}

		public Node build(){
			return new Node(ip, port);
		}
	}

	public String getAddr() {
		return this.ip + this.port;
	}

	@Override
	public String toString() {
		return "ClientNode{" +
				"ip='" + ip + '\'' +
				", port=" + port +
				'}';
	}
}
