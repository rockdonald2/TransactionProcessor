package edu.pay.metrics;

import edu.utils.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MetricsOutput {

	/**
	 * Kiírja formázott JSON-ként az adott PayMetrics instancia állapotát a megadott File-ba.
	 * @param metrics
	 * 								mutatók
	 * @param file
	 *                              kimeneti File adatfolyama
	 */
	public static void writeToFile(PayMetrics metrics, FileOutputStream file) {
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
			// TODO: exception
		}
	}

}
