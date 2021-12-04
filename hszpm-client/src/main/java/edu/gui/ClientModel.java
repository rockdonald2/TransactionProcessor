package edu.gui;

import edu.cnp.parts.CnpParts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class ClientModel {

	private String csvInputPath;
	private String jsonOutputPath;
	private boolean wrongJsonInput = true;
	private boolean wrongCsvInput = true;

	private Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers;

	public Map<CnpParts, ArrayList<BigDecimal>> getMapOfCustomers() {
		return mapOfCustomers;
	}

	public void setMapOfCustomers(Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		this.mapOfCustomers = mapOfCustomers;
	}

	public BigDecimal[] getClientPayments(CnpParts cnp) {
		return mapOfCustomers.get(cnp).toArray(new BigDecimal[0]);
	}

	public String getCsvInputPath() {
		return csvInputPath;
	}

	public void setCsvInputPath(String csvInputPath) {
		this.csvInputPath = csvInputPath;
	}

	public String getJsonOutputPath() {
		return jsonOutputPath;
	}

	public void setJsonOutputPath(String jsonOutputPath) {
		this.jsonOutputPath = jsonOutputPath;
	}

	public boolean isWrongJsonInput() {
		return wrongJsonInput;
	}

	public void setWrongJsonInput(boolean wrongJsonInput) {
		this.wrongJsonInput = wrongJsonInput;
	}

	public boolean isWrongCsvInput() {
		return wrongCsvInput;
	}

	public void setWrongCsvInput(boolean wrongCsvInput) {
		this.wrongCsvInput = wrongCsvInput;
	}

	public boolean validInputs() {
		return !wrongJsonInput && !wrongCsvInput;
	}

}
