package edu.pay.processor;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import edu.pay.error.PayError;
import edu.pay.exception.general.metrics.MetricsException;
import edu.pay.exception.general.metrics.MetricsOutputException;
import edu.pay.metrics.MetricsOutput;
import edu.pay.metrics.PayMetricsFactory;
import edu.pay.metrics.SimplePayMetrics;
import edu.pay.processor.dataloader.DataLoader;
import edu.pay.processor.dataloader.DataLoaderFactory;
import edu.pay.processor.utils.ProcessorUtils;
import edu.pay.utils.PayUtils;
import edu.cnp.CnpParts;
import edu.utils.Logger;

class SimplePayProcessorImpl implements PayProcessor {

	private final Set<PayError> errors = new HashSet<>();

	@Override
	public Map<CnpParts, ArrayList<BigDecimal>> process(FileInputStream paymentsInputStream, FileOutputStream metricsOutputStream) throws IOException {
		DataLoaderFactory loaderFactory = new DataLoaderFactory();
		DataLoader loader = loaderFactory.getLoader("csv");
		var dataInput = loader.loadData(paymentsInputStream);
		var mapOfCustomers = ProcessorUtils.getCustomers(dataInput, errors);

		PayMetricsFactory metricsFactory = new PayMetricsFactory();
		try {
			SimplePayMetrics metrics = (SimplePayMetrics) metricsFactory.getMetrics("simple");

			if (mapOfCustomers.size() != 0) {
				var averagePaymentAmount = getAverage(mapOfCustomers);
				var bigPayments = getBigPaymentsNumber(mapOfCustomers);
				var paymentsByMinors = getPaymentsByMinors(mapOfCustomers);
				var smallPayments = getSmallPaymentsNumber(mapOfCustomers);
				var totalAmountCapitalCity = getTotalAmountCapitalCity(mapOfCustomers);
				var foreigners = getForeigners(mapOfCustomers);

				metrics.setAveragePaymentAmount(averagePaymentAmount);
				metrics.setBigPayments(bigPayments);
				metrics.setPaymentsByMinors(paymentsByMinors);
				metrics.setSmallPayments(smallPayments);
				metrics.setTotalAmountCapitalCity(totalAmountCapitalCity);
				metrics.setForeigners(foreigners);
			} else {
				metrics.setAllMetricesNullOrEmpty();
			}

			MetricsOutput.writeToFile(metrics, metricsOutputStream);
		} catch (MetricsException | MetricsOutputException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
		}

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
		final var currentYear = Calendar.getInstance().get(Calendar.YEAR);

		return (int) mapOfCustomers.keySet().stream().filter(k -> (currentYear - k.birthDate().year()) <= 18).count();
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
		return mapOfCustomers.keySet().stream().filter(k -> k.county().getAbrv().equals("BU") && !k.foreigner())
						.map(k -> mapOfCustomers.get(k).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
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
		return (int) mapOfCustomers.keySet().stream().filter(CnpParts::foreigner).count();
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
		final var upperLimit = new BigDecimal(limit);

		return (int) mapOfCustomers.keySet().stream().map(k -> mapOfCustomers.get(k).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
						.filter(k -> k.compareTo(upperLimit) <= 0).count();
	}

	Integer getSmallPaymentsNumber(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return getPaymentsNumberByLimit(mapOfCustomers, "5000");
	}

	Integer getBigPaymentsNumber(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return PayUtils.getTotalTransactionNumber(mapOfCustomers) - getPaymentsNumberByLimit(mapOfCustomers, "5000");
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
		return PayUtils.sumTransactions(mapOfCustomers).divide(BigDecimal.valueOf(PayUtils.getTotalTransactionNumber(mapOfCustomers)), 2, RoundingMode.HALF_EVEN);
	}

}
