package edu.network.exception;

public abstract class ClientException extends RuntimeException {

  public ClientException(String errorMsg) {
    super(errorMsg);
  }

}
