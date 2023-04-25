package cn.starboot.socket.maintain;

import cn.starboot.socket.factory.Configuration;
import cn.starboot.socket.factory.ConfigurationFactory;

import java.io.File;
import java.net.URL;
import java.util.*;

public class MaintainManager {

	/**
	 * 待装配的配置文件名字
	 */
	private static final String DEFAULT_CLASSPATH_CONFIGURATION_FILE = "maintain.properties";

	/**
	 * 通用cmd处理命令与命令码的Map映射
	 */
	private static final Map<MaintainEnum, AbstractMaintain> handlerMap = new HashMap<>();

	private static MaintainManager maintainManager;

	private MaintainManager() {
	}

	public static synchronized MaintainManager getInstance() {
		if (maintainManager == null) {
			maintainManager = new MaintainManager();
		}
		return maintainManager;
	}

	static {
		try {
			URL url = MaintainManager.class.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
			List<Configuration> configurations = ConfigurationFactory.parseConfiguration(url);
			if (configurations == null) {
				configurations = ConfigurationFactory.parseConfiguration(new File(url.getPath()));
			}
			if (configurations != null) {
				init(configurations);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void init(List<Configuration> configurations) throws Exception {
		for (Configuration configuration : configurations) {
			AbstractMaintain cmdHandler = (AbstractMaintain) (Class.forName(configuration.getPath())).newInstance();
			registerCommand(cmdHandler);
		}
	}

	private static void registerCommand(AbstractMaintain imCommandHandler) throws Exception {
		if (imCommandHandler == null || imCommandHandler.getName() == null) {
			return;
		}
		MaintainEnum command = imCommandHandler.getName();
		if (Objects.isNull(handlerMap.get(command))) {
			handlerMap.put(command, imCommandHandler);
		} else {
			throw new Exception("cmd code:" + command + ",has been registered, please correct!");
		}
	}

	public <T> T getCommand(MaintainEnum command, Class<T> clazz) {
		AbstractMaintain cmdHandler = getCommand(command);
		if (cmdHandler != null) {
			return (T) cmdHandler;
		}
		return null;
	}

	public AbstractMaintain getCommand(MaintainEnum command) {
		if (command == null) {
			return null;
		}
		return handlerMap.get(command);
	}


}
