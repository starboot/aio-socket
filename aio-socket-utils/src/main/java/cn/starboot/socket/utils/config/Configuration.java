package cn.starboot.socket.utils.config;

import java.util.Properties;

public final class Configuration {

	private final String name;

	private final String[] value;

	protected Configuration(String name, Properties prop) {
		this.name = name;
		this.value = prop.getProperty(name).split(",");
	}

	public String getName() {
		return name;
	}

	public String[] getValue() {
		return value;
	}
}
