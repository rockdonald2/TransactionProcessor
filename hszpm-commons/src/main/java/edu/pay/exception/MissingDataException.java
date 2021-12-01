package edu.pay.exception;

public class MissingDataException extends PayException {

	public MissingDataException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_LINE);
	}

}
