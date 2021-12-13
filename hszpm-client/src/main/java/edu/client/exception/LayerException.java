package edu.client.exception;

import edu.network.exception.ClientException;

public class LayerException extends ClientException {

  public LayerException(String errorMsg) {
    super(errorMsg);
  }

}
