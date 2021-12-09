package edu.exception;

public class RequestProcessFailureException extends ClientException {

  public RequestProcessFailureException(String errorMsg) {
    super(errorMsg);
  }

}
