package edu.pay.exception.general;

public abstract class GeneralException extends RuntimeException {

	public GeneralException(String errorMsg) {
		super("Error: " + errorMsg);
	}

	public GeneralException(GeneralException e) {
		super(e.getMessage());
	}

}
