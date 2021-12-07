package edu.pay.metrics.dataoutputter;

import edu.pay.exception.general.UnsupportedOutputException;
import edu.pay.metrics.dataoutputter.json.JSONOutput;

import java.util.Objects;

public class MetricsOutputFactory {

	public MetricsOutput getWriter(String type) {
		if (Objects.isNull(type)) {
			throw new UnsupportedOutputException();
		}

		if (type.equalsIgnoreCase("json")) {
			return new JSONOutput();
		}

		throw new UnsupportedOutputException();
	}

}
