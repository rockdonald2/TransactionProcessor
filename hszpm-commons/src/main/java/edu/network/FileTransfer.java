package edu.network;

import java.io.Serializable;

public class FileTransfer implements Serializable {

  private byte[] data;

  public FileTransfer(final byte[] data) {
    this.data = data;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

}
