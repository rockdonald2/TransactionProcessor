package edu.network.exceptions;

public class SocketFailureException extends ServerException {

  public SocketFailureException(String errorMsg) {
    super(errorMsg);
  }

}
