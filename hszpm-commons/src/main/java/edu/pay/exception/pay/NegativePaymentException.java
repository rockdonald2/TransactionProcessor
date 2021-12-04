package edu.pay.exception.pay;

public class NegativePaymentException extends PayException {

	public NegativePaymentException(String errorMsg) {
		super(errorMsg, ErrorCode.INVALID_PAYMENT);
	}

}
