package edu.pay.metrics;

import edu.pay.exception.general.UnsupportedMetricsTypeException;

import java.util.Objects;

public class PayMetricsFactory {

	public PayMetrics getMetrics(String type) {
		if (Objects.isNull(type)) {
			throw new UnsupportedMetricsTypeException();
		}

		if (type.equalsIgnoreCase("SIMPLE")) {
			return new SimplePayMetricsImpl();
		}

		throw new UnsupportedMetricsTypeException();
	}

}
