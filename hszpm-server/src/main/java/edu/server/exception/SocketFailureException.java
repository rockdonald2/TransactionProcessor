package edu.server.exception;

import edu.network.exception.ServerException;

public class SocketFailureException extends ServerException {

  public SocketFailureException(String errorMsg) {
    super(errorMsg);
  }

}
