package edu.server.utils;

import edu.utils.Logger;
import edu.server.utils.exception.PropertyProviderException;

import java.io.*;
import java.util.Properties;

public class PropertyProvider {

    private static final Properties properties;

    static {
        properties = new Properties();

        try (InputStream is = PropertyProvider.class.getResourceAsStream("/server_config.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Stream of config resource is null.");
            }
        } catch (IOException | NullPointerException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to access config resource.");
            Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
        }
    }

    public static synchronized String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static synchronized void setProperty(String key, String value) throws PropertyProviderException {
        properties.setProperty(key, value);
    }

}