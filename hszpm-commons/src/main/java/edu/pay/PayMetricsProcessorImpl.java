package edu.pay;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import edu.cnp.CnpValidator;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import edu.pay.exception.MissingDataException;
import edu.pay.exception.NegativePaymentException;
import edu.pay.exception.PayException;
import edu.cnp.exception.CnpException;
import edu.cnp.CnpParts;
import edu.utils.Logger;


class PayMetricsProcessorImpl implements PayMetricsProcessor {

	private final Set<PayError> errors = new HashSet<>();

	@Override
	public Map<CnpParts, ArrayList<BigDecimal>> process(FileInputStream paymentsInputStream, FileOutputStream metricsOutputStream) throws IOException {
		var dataInput = loadData(paymentsInputStream);

		var mapOfCustomers = getCustomers(dataInput);

		PayMetrics metrics;
		if (mapOfCustomers.size() != 0) {
			var averagePaymentAmount = getAverage(mapOfCustomers);

			var bigPayments = getBigPaymentsNumber(mapOfCustomers);

			var paymentsByMinors = getPaymentsByMinors(mapOfCustomers);

			var smallPayments = getSmallPaymentsNumber(mapOfCustomers);

			var totalAmountCapitalCity = getTotalAmountCapitalCity(mapOfCustomers);

			var foreigners = getForeigners(mapOfCustomers);

			metrics = PayMetrics.getMetrics(foreigners, paymentsByMinors, bigPayments,
							smallPayments, averagePaymentAmount, totalAmountCapitalCity, errors);
		} else {
			metrics = PayMetrics.getMetrics(0, 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO, errors);
		}

		metrics.writeToFile(metricsOutputStream);
		return mapOfCustomers;
	}

	/**
	 * Visszatéríti azon fizetések számát, amelyet 18 év alattiak intéztek.
	 *
	 * @param mapOfCustomers
	 *                          tranzakciók
	 * @return
	 *          fizetések száma
	 */
	int getPaymentsByMinors(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		int counter = 0;
		final var currentYear = Calendar.getInstance().get(Calendar.YEAR);

		for (var customer : mapOfCustomers.keySet()) {
			if (currentYear - customer.birthDate().year() <= 18) {
				counter++;
			}
		}

		return counter;
	}

	/**
	 * Visszatéríti a bukaresti születésű román állampolgárok által intézett fizetések összegét.
	 *
	 * @param mapOfCustomers
	 *                          tranzakciók
	 * @return
	 *          összeg
	 */
	BigDecimal getTotalAmountCapitalCity(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		var sum = BigDecimal.ZERO;

		for (var customer : mapOfCustomers.keySet()) {
			if (customer.county().getAbrv().equals("BU") && !customer.foreigner()) {
				for (var v : mapOfCustomers.get(customer)) {
					sum = sum.add(v);
				}
			}
		}

		return sum;
	}

	/**
	 * Külföldi személyek száma, akik fizetést intéztek.
	 *
	 * @param mapOfCustomers
	 *                          tranzakciók
	 * @return
	 *          külföldi személyek száma
	 */
	Integer getForeigners(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		Set<String> coll = new HashSet<>();

		for (var customer : mapOfCustomers.keySet()) {
			if (customer.foreigner()) {
				coll.add(customer.toString());
			}
		}

		return coll.size();
	}

	/**
	 * Visszatéríti a LIMIT-ig érvényes fizetések számát.
	 *
	 * @param mapOfCustomers
	 *                          tranzakciók
	 * @param limit
	 *              határérték
	 * @return
	 *          fizetések száma
	 */
	Integer getPaymentsNumberByLimit(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers, final String limit) {
		int counter = 0;
		final var upperLimit = new BigDecimal(limit);

		for (var customer : mapOfCustomers.keySet()) {
			for (var v : mapOfCustomers.get(customer)) {
				if (v.compareTo(upperLimit) <= 0) {
					counter++;
				}
			}
		}

		return counter;
	}

	Integer getSmallPaymentsNumber(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return getPaymentsNumberByLimit(mapOfCustomers, "5000");
	}

	Integer getBigPaymentsNumber(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return PayUtils.getTotalTranzactionNumber(mapOfCustomers) - getPaymentsNumberByLimit(mapOfCustomers, "5000");
	}

	/**
	 * Visszatéríti a fizetések átlagát.
	 *
	 * @param mapOfCustomers
	 *                          tranzakciók
	 * @return
	 *          fizetések átlaga
	 */
	BigDecimal getAverage(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return PayUtils.sumTranzactions(mapOfCustomers).divide(BigDecimal.valueOf(PayUtils.getTotalTranzactionNumber(mapOfCustomers)), 2, RoundingMode.HALF_EVEN);
	}

	/**
	 * Beolvassa a tranzakciók listáját egy CSV állományból, amely mezőelválasztoként a ';'-t használja.
	 *
	 * @param paymentsInputStream
	 *                              CSV állomány
	 * @return
	 *          állomány sorai
	 * @throws IOException
	 *                      ha az állomány nem található, vagy beolvasási hiba lépett fel
	 */
	List<String[]> loadData(final InputStream paymentsInputStream) throws IOException {
		var csvParser = new CSVParserBuilder()
						.withSeparator(';')
						.build();

		List<String[]> rows;
		try (var reader = new CSVReaderBuilder(new InputStreamReader(paymentsInputStream))
						.withCSVParser(csvParser)
						.build()) {
			rows = reader.readAll();
		} catch (CsvException e) {
			throw new IOException(e.getMessage());
		}

		return rows;
	}

	/**
	 * Ellenőrzi a fizetésekhez tartozó CNP-ket, és visszatéríti a fizetéseket.
	 *
	 * @param dataInput
	 *                  CSV állomány sorai
	 * @return
	 *          tranzakciók
	 */
	Map<CnpParts, ArrayList<BigDecimal>> getCustomers(final List<String[]> dataInput) {
		var validator = CnpValidator.getValidator();
		var mapOfCustomers = new HashMap<CnpParts, ArrayList<BigDecimal>>();

		for (int i = 0; i < dataInput.size(); i++) {
			final var currentPayment = dataInput.get(i);

			if (currentPayment.length != 2) {
				continue;
			}

			CnpParts cnp = null;
			BigDecimal paymentAmount;

			try {
				if (currentPayment[0].equals("") || currentPayment[1].equals("")) {
					throw new MissingDataException("Missing data from line");
				}

				paymentAmount = new BigDecimal(currentPayment[1]);

				if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
					throw new NegativePaymentException("Invalid payment with negative value");
				}
			} catch (PayException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
				writeError(i + 1, e.getCodeType());
				continue;
			}

			for (var j : mapOfCustomers.keySet()) {
				if (j.toString().equals(currentPayment[0])) {
					cnp = j;
				}
			}

			if (cnp == null) {
				try {
					cnp = validator.validateCnp(currentPayment[0]);
				} catch (CnpException e) {
					Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
					writeError(i + 1, e.getCodeType());
					continue;
				}
			}

			if (!mapOfCustomers.containsKey(cnp)) {
				mapOfCustomers.put(cnp, new ArrayList<>());
			}

			mapOfCustomers.get(cnp).add(paymentAmount);
		}

		return mapOfCustomers;
	}

	/**
	 * Kiír egy hibapéldányt a hibatömbbe, együtt annak típusával és sorszámával, ahol a hiba előfordult.
	 *
	 * @param lineNumber
	 *                      sor
	 * @param errorType
	 *                      hibatípus
	 */
	private void writeError(final int lineNumber, final int errorType) {
		errors.add(PayError.generateError(lineNumber, errorType));
	}

}
