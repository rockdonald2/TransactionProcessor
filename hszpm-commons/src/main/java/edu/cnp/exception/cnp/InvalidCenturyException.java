package edu.cnp.exception.cnp;

public class InvalidCenturyException extends CnpException {

	public InvalidCenturyException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_CNP);
	}

}
