package edu.cnp.exception;

public class InvalidControlNumberException extends CnpException {

	public InvalidControlNumberException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_CNP);
	}

}
