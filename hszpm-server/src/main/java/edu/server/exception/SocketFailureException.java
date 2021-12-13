package edu.server.exception;

public class SocketFailureException extends ServerException {

  public SocketFailureException(String errorMsg) {
    super(errorMsg);
  }

}
