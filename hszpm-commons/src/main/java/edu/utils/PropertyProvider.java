package edu.utils;

import edu.utils.exception.PropertyProviderException;

import java.io.*;
import java.util.Properties;

public class PropertyProvider {

  private static final Properties serverProperties;
  private static final Properties clientProperties;

  static {
    clientProperties = new Properties();
    serverProperties = new Properties();

    try (InputStream is = PropertyProvider.class.getResourceAsStream("/server_properties.properties")) {
      if (is != null) {
        serverProperties.load(is);
      } else {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Stream of resource is null.");
      }
    } catch (IOException e) {
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to access resource.");
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
    }

    try (InputStream is = PropertyProvider.class.getResourceAsStream("/client_properties.properties")) {
      if (is != null) {
        clientProperties.load(is);

        if (new File(clientProperties.getProperty("config.file")).exists()) {
          clientProperties.load(new FileInputStream(clientProperties.getProperty("config.file")));
        }
      } else {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Stream of resource is null.");
      }
    } catch (IOException e) {
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to access resource.");
      Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
    }
  }

  public static synchronized String getClientProperty(String key) {
    return clientProperties.getProperty(key);
  }

  public static String getServerProperty(String key) {
    return serverProperties.getProperty(key);
  }

  public static synchronized void setClientProperty(String key, String value, boolean save) throws PropertyProviderException {
    clientProperties.setProperty(key, value);

    if (save) {
      try (OutputStream out = new FileOutputStream(clientProperties.getProperty("config.file"), false)) {
        clientProperties.store(out, null);
      } catch (IOException e) {
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, "Failed to write resource.");
        Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
        throw new PropertyProviderException();
      }
    }
  }

}

