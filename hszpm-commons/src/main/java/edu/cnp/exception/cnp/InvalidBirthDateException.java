package edu.cnp.exception.cnp;

public class InvalidBirthDateException extends CnpException {

	public InvalidBirthDateException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_CNP);
	}

}
