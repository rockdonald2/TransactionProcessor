package edu.pay.processor.dataloader.csv;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import edu.pay.processor.dataloader.DataLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class CsvLoader implements DataLoader {

	/**
	 * Beolvassa a tranzakciók listáját egy CSV állományból, amely mezőelválasztoként a ';'-t használja.
	 *
	 * @param is
	 *                              CSV állomány
	 * @return
	 *          állomány sorai
	 * @throws IOException
	 *                      ha az állomány nem található, vagy beolvasási hiba lépett fel
	 */
	@Override
	public List<String[]> loadData(InputStream is) {
		// TODO: load data factory
		var csvParser = new CSVParserBuilder()
						.withSeparator(';')
						.build();

		List<String[]> rows = null;
		try (var reader = new CSVReaderBuilder(new InputStreamReader(is))
						.withCSVParser(csvParser)
						.build()) {
			rows = reader.readAll();
		} catch (CsvException | IOException e) {
			// TODO: exception
		}

		if (Objects.isNull(rows)) {
			// TODO: exception
		}

		return rows;
	}
}
