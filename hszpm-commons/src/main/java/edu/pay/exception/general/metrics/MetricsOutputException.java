package edu.pay.exception.general.metrics;

import edu.pay.exception.general.GeneralException;

public class MetricsOutputException extends GeneralException {

	public MetricsOutputException() {
		super("Failed to write metrices to output.");
	}

}
