package edu.pay.processor;

import edu.pay.exception.general.UnsupportedProcessorTypeException;

import java.util.Objects;

public class PayProcessorFactory {

	public PayProcessor getProcessor(String type) throws UnsupportedProcessorTypeException {
		if (Objects.isNull(type)) {
			throw new UnsupportedProcessorTypeException();
		}

		if (type.equalsIgnoreCase("SIMPLE")) {
			return new SimplePayProcessorImpl();
		}

		throw new UnsupportedProcessorTypeException();
	}

}
