package edu.network;

import java.io.Serializable;
import java.util.Properties;

public class FileTransfer implements Serializable {

  private byte[] data;
  private Properties config;

  public FileTransfer(final byte[] data) {
    this.data = data;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public void setProperties(Properties props) {
    config = props;
  }

  public Properties getProperties() {
    return config;
  }

}
