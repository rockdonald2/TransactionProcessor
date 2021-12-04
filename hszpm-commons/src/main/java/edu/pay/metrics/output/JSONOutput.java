package edu.pay.metrics.output;

import edu.pay.exception.general.metrics.MetricsOutputException;
import edu.pay.metrics.MetricsOutput;
import edu.pay.metrics.PayMetrics;
import edu.utils.Logger;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class JSONOutput implements MetricsOutput {

	/**
	 * Kiírja formázott JSON-ként az adott PayMetrics instancia állapotát a megadott File-ba.
	 * @param metrics
	 * 								mutatók
	 * @param file
	 *                              kimeneti File adatfolyama
	 */
	@Override
	public void writeToFile(PayMetrics metrics, @Nullable FileOutputStream file) throws MetricsOutputException {
		if (Objects.isNull(file)) return;

		var output = new JSONObject();
		var metrices = metrics.metrices();

		metrices.keySet().forEach(k -> {
			output.put(k, metrices.get(k).getLeft());
		});

		var errs = new JSONArray();
		for (var e : metrics.errors()) {
			errs.put(e.generateJson());
		}
		output.put("errors", errs);

		var o = new OutputStreamWriter(file);
		try {
			o.write(metrics.toString());
			o.flush();
			o.close();
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
			throw new MetricsOutputException();
		}
	}

}
