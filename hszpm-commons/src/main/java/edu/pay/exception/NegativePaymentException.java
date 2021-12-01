package edu.pay.exception;

public class NegativePaymentException extends PayException {

	public NegativePaymentException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_PAYMENT);
	}

}
