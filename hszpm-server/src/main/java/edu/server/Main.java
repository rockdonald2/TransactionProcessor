package edu.server;

import edu.utils.PropertyProvider;

public class Main {
  public static void main(String[] args) {
    new ServerFactory().getServer(PropertyProvider.getProperty("server.type")).startServer();
  }
}
