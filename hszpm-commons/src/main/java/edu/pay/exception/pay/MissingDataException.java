package edu.pay.exception.pay;

public class MissingDataException extends PayException {

	public MissingDataException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_LINE);
	}

}
