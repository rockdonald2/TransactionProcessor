package edu.client.exception;

import edu.network.exception.ClientException;

public class RequestProcessFailureException extends ClientException {

  public RequestProcessFailureException(String errorMsg) {
    super(errorMsg);
  }

}
