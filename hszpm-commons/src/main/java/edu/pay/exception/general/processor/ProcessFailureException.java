package edu.pay.exception.general.processor;

import edu.pay.exception.general.GeneralException;

public class ProcessFailureException extends GeneralException {

	public ProcessFailureException(String errorMsg) {
		super(errorMsg);
	}

}
