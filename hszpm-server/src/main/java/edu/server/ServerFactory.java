package edu.server;

import edu.server.exception.UnsupportedServerTypeException;
import edu.server.network.NetworkServer;

import java.util.Objects;

public class ServerFactory {
  public Server getServer(String type) {
    if (Objects.isNull(type)) {
      throw new UnsupportedServerTypeException();
    }

    if (type.equalsIgnoreCase("NETWORK")) {
      return NetworkServer.getInstance();
    }

    throw new UnsupportedServerTypeException();
  }
}
