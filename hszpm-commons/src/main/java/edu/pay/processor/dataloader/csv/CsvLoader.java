package edu.pay.processor.dataloader.csv;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import edu.pay.exception.general.loader.LoaderException;
import edu.pay.processor.dataloader.DataLoader;

import java.io.*;
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
	 * @throws LoaderException
	 *                      ha az állomány nem található, vagy beolvasási hiba lépett fel
	 */
	@Override
	public List<String[]> loadData(InputStream is) throws LoaderException {
		var csvParser = new CSVParserBuilder()
						.withSeparator(';')
						.build();

		List<String[]> rows = null;
		try (var reader = new CSVReaderBuilder(new InputStreamReader(is))
						.withCSVParser(csvParser)
						.build()) {
			rows = reader.readAll();
		} catch (CsvException | IOException e) {
			throw new LoaderException("Failed to parse CSV lines.");
		}

		if (Objects.isNull(rows)) {
			throw new LoaderException("Failed to parse CSV lines.");
		}

		return rows;
	}
}
