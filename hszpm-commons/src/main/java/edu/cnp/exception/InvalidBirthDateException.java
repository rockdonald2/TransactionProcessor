package edu.cnp.exception;

public class InvalidBirthDateException extends CnpException {

	public InvalidBirthDateException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_CNP);
	}

}
