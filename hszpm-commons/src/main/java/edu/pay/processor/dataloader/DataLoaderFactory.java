package edu.pay.processor.dataloader;

import edu.pay.processor.dataloader.csv.CsvLoader;

import java.util.Objects;

public class DataLoaderFactory {

	public DataLoader getLoader(String type) {
		if (Objects.isNull(type)) {
			// TODO: exception
			throw new RuntimeException();
		}

		if (type.equalsIgnoreCase("csv")) {
			return new CsvLoader();
		}

		throw new RuntimeException();
	}

}
