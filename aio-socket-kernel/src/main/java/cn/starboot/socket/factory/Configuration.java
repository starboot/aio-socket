package cn.starboot.socket.factory;

import java.util.Properties;

public final class Configuration {

	private int code;

	private  String path;

	public Configuration(String cmd, Properties prop) {
		this.code = Integer.parseInt(cmd);
		String[] values = prop.getProperty(cmd).split(",");
		if(values.length > 0){
			path = values[0];
			if(values.length >1){
				for(int i = 0 ; i < values.length ; i++){
					if(i > 0) {
//                        cmdProcessors.add(values[i]);
					}
				}
			}
		}
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
