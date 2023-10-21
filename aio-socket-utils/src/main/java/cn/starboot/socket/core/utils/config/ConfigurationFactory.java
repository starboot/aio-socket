package cn.starboot.socket.core.utils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class ConfigurationFactory {

	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationFactory.class);

	public static List<Configuration> parseConfiguration(final URL url) throws Exception {
		List<Configuration> configurations;
		InputStream input = null;
		try {
			input = url.openStream();
			configurations = parseConfiguration(input);
		} catch (Exception e) {
			throw new Exception("Error configuring from " + url + ". Initial cause was " + e.getMessage(), e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return configurations;
	}

	public static List<Configuration> parseConfiguration(final File file) throws Exception {
		if (file == null) {
			throw new Exception("Attempt to configure command from null file.");
		}
		List<Configuration> configurations;
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file));
			configurations = parseConfiguration(input);
		} catch (Exception e) {
			throw new Exception("Error configuring from " + file + ". Initial cause was " + e.getMessage(), e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return configurations;
	}

	public static List<Configuration> parseConfiguration(final InputStream inputStream) throws Exception {

		List<Configuration> configurations = new ArrayList<>();
		try {
			Properties props = new Properties();
			props.load(inputStream);
			for(String key : props.stringPropertyNames()){
				configurations.add(new Configuration(key , props));
			}
		} catch (Exception e) {
			throw new Exception("Error configuring from input stream. Initial cause was " + e.getMessage(), e);
		}
		return configurations;
	}
}
