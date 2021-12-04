package edu.cnp.exception.cnp;

public class InvalidControlNumberException extends CnpException {

	public InvalidControlNumberException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_CNP);
	}

}
