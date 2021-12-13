package edu.server.exception;

import edu.network.exception.ServerException;

public class IncompatibleFileTypeException extends ServerException {

  public IncompatibleFileTypeException(String errorMsg) {
    super(errorMsg);
  }

}
