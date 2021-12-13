package edu.server.exception;

public class UnsupportedServerTypeException extends ServerException {
  public UnsupportedServerTypeException() {
    super("Unsupported server type.");
  }
}
