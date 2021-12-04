package edu.cnp.exception.cnp;

public class InvalidOrderNumberException extends CnpException {

	public InvalidOrderNumberException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_CNP);
	}

}
