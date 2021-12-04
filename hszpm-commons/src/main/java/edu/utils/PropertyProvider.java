package edu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyProvider {

	private static final Properties properties;

	static {
		properties = new Properties();
		try (InputStream is = PropertyProvider.class.getResourceAsStream("/properties.properties")) {
			if (is != null) {
				properties.load(is);
			}
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to access resource.");
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

}

