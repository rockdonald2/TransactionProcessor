package edu.pay.metrics.dataoutputter.json;

import edu.network.FileTransfer;
import edu.pay.exception.general.metrics.MetricsOutputException;
import edu.pay.metrics.dataoutputter.MetricsOutput;
import edu.pay.metrics.PayMetrics;
import edu.utils.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class JSONOutput implements MetricsOutput {

	/**
	 * Kiírja formázott JSON-ként az adott PayMetrics instancia állapotát a megadott File-ba.
   * @param metrics
   * 								mutatók
   * @param os
   */
	@Override
	public void writeToStream(PayMetrics metrics, @NotNull ObjectOutputStream os) throws MetricsOutputException {
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

		try {
			FileTransfer out = new FileTransfer(output.toString().getBytes(StandardCharsets.UTF_8));
			os.writeObject(out);
			os.flush();
		} catch (IOException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
			throw new MetricsOutputException();
		}
	}

}
