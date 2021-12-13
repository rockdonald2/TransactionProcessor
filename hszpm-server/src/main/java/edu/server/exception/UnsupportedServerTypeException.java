package edu.server.exception;

import edu.network.exception.ServerException;

public class UnsupportedServerTypeException extends ServerException {
  public UnsupportedServerTypeException() {
    super("Unsupported server type.");
  }
}
