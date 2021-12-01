package edu.cnp.exception;

public class InvalidSexException extends CnpException {

	public InvalidSexException(String errorMsg) {
		super(errorMsg, CnpException.ErrorCode.INVALID_CNP);
	}

}
