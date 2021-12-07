package edu.pay.metrics.dataoutputter;

import edu.pay.exception.general.metrics.MetricsOutputException;
import edu.pay.metrics.PayMetrics;
import org.jetbrains.annotations.Nullable;

import java.io.FileOutputStream;

public interface MetricsOutput {

	void writeToFile(PayMetrics metrics, @Nullable FileOutputStream file) throws MetricsOutputException;

}
