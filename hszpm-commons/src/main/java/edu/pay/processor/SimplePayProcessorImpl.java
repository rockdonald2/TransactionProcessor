package edu.pay.processor;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import edu.pay.error.PayError;
import edu.pay.exception.general.metrics.MetricsException;
import edu.pay.exception.general.metrics.MetricsOutputException;
import edu.pay.metrics.MetricsOutputFactory;
import edu.pay.metrics.output.JSONOutput;
import edu.pay.metrics.PayMetrics;
import edu.pay.metrics.PayMetricsFactory;
import edu.pay.metrics.SimplePayMetrics;
import edu.pay.processor.dataloader.DataLoader;
import edu.pay.processor.dataloader.DataLoaderFactory;
import edu.pay.processor.utils.ProcessorUtils;
import edu.pay.utils.PayUtils;
import edu.cnp.parts.CnpParts;
import edu.utils.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SimplePayProcessorImpl implements PayProcessor {

	private final Set<PayError> errors = new HashSet<>();
	private SimplePayMetrics metrics;

	@Override
	public Map<CnpParts, ArrayList<BigDecimal>> process(@NotNull FileInputStream paymentsInputStream, @Nullable FileOutputStream metricsOutputStream) {
		DataLoaderFactory loaderFactory = new DataLoaderFactory();
		DataLoader loader = loaderFactory.getLoader("csv");
		var dataInput = loader.loadData(paymentsInputStream);
		var mapOfCustomers = ProcessorUtils.getCustomers(dataInput, errors);

		PayMetricsFactory metricsFactory = new PayMetricsFactory();
		try {
			metrics = (SimplePayMetrics) metricsFactory.getMetrics("simple");

			metrics.setErrors(errors);

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

			MetricsOutputFactory outputFactory = new MetricsOutputFactory();
			outputFactory.getWriter("json").writeToFile(metrics, metricsOutputStream);
		} catch (MetricsException | MetricsOutputException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
		}

		return mapOfCustomers;
	}

	@Override
	public PayMetrics getProcessedMetrics() {
		return metrics;
	}

	/**
	 * Visszatéríti azon fizetések számát, amelyet 18 év alattiak intéztek.
	 *
	 * @param mapOfCustomers
	 *                          tranzakciók
	 * @return
	 *          fizetések száma
	 */
	private int getPaymentsByMinors(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
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
	private BigDecimal getTotalAmountCapitalCity(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
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
	private Integer getForeigners(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
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
	private Integer getPaymentsNumberByLimit(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers, final String limit) {
		final var upperLimit = new BigDecimal(limit);

		return (int) mapOfCustomers.keySet().stream().map(k -> mapOfCustomers.get(k).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
						.filter(k -> k.compareTo(upperLimit) <= 0).count();
	}

	private Integer getSmallPaymentsNumber(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return getPaymentsNumberByLimit(mapOfCustomers, "5000");
	}

	private Integer getBigPaymentsNumber(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
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
	private BigDecimal getAverage(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return PayUtils.sumTransactions(mapOfCustomers).divide(BigDecimal.valueOf(PayUtils.getTotalTransactionNumber(mapOfCustomers)), 2, RoundingMode.HALF_EVEN);
	}

}
