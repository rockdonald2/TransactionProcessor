package edu.utils;

import java.io.*;
import java.util.Properties;

public class PropertyProvider {

	private static final Properties properties;

	static {
		properties = new Properties();
		try (InputStream is = PropertyProvider.class.getResourceAsStream("/properties.properties")) {
			if (is != null) {
				properties.load(is);

				if (new File(properties.getProperty("config.file")).exists()) {
					properties.load(new FileInputStream(properties.getProperty("config.file")));
				}
			}
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to access resource.");
		}
	}

	public static synchronized String getProperty(String key) {
		return properties.getProperty(key);
	}
	public static synchronized void setProperty(String key, String value) {
		properties.setProperty(key, value);

		try (OutputStream out = new FileOutputStream(properties.getProperty("config.file"))) {
			properties.store(out, null);
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to write resource.");
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
		}
	}

}

