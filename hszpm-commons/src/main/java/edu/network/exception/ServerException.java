package edu.network.exception;

public abstract class ServerException extends RuntimeException {

  public ServerException() {
    super();
  }

  public ServerException(String errorMsg) {
    super(errorMsg);
  }

  public ServerException(String errorMsg, Exception e) {
    super(errorMsg, e);
  }

}
