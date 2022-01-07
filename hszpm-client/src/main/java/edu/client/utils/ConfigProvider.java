package edu.client.utils;

import edu.client.utils.exception.ConfigProviderException;
import edu.utils.Logger;

import java.io.*;
import java.util.Properties;

public class ConfigProvider {

    private static Properties properties;
    private static final String CONFIG_PATH = System.getProperty("user.dir") + "\\config.properties";

    public static synchronized void load() {
        properties = new Properties();

        try (InputStream is = ConfigProvider.class.getResourceAsStream("/default_config.properties")) {
            if (is != null) {
                properties.load(is);

                File configFile = new File(CONFIG_PATH);
                if (configFile.exists()) {
                    properties.load(new FileInputStream(configFile));
                } else {
                    configFile.createNewFile();
                    properties.store(new FileOutputStream(configFile), null);
                    throw new ConfigProviderException("Failed to access config resource.");
                }
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

    public static synchronized void setProperty(String key, String value, boolean save) throws ConfigProviderException {
        properties.setProperty(key, value);

        if (save) {
            try (OutputStream out = new FileOutputStream(CONFIG_PATH, false)) {
                properties.store(out, null);
            } catch (IOException e) {
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to write config resource.");
                Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
                throw new ConfigProviderException("Failed to write config resource.");
            }
        }
    }

}


