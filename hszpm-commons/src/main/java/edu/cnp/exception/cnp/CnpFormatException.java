package edu.cnp.exception.cnp;

public class CnpFormatException extends CnpException {

	public CnpFormatException(String errorMsg) {
		super(errorMsg, CnpException.ErrorCode.INVALID_CNP);
	}

}
