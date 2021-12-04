package edu.pay.processor.dataloader;

import edu.pay.exception.general.UnsupportedLoaderTypeException;
import edu.pay.processor.dataloader.csv.CsvLoader;

import java.util.Objects;

public class DataLoaderFactory {

	public DataLoader getLoader(String type) throws UnsupportedLoaderTypeException {
		if (Objects.isNull(type)) {
			throw new UnsupportedLoaderTypeException();
		}

		if (type.equalsIgnoreCase("csv")) {
			return new CsvLoader();
		}

		throw new UnsupportedLoaderTypeException();
	}

}
