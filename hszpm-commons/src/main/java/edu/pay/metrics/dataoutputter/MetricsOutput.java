package edu.pay.metrics.dataoutputter;

import edu.pay.exception.general.metrics.MetricsOutputException;
import edu.pay.metrics.PayMetrics;
import org.jetbrains.annotations.NotNull;

import java.io.ObjectOutputStream;

public interface MetricsOutput {

	void writeToStream(PayMetrics metrics, @NotNull ObjectOutputStream os) throws MetricsOutputException;

}
