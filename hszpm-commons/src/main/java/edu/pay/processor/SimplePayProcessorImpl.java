package edu.pay.processor;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import edu.pay.error.PayError;
import edu.pay.exception.general.UnsupportedMetricsTypeException;
import edu.pay.exception.general.UnsupportedOutputException;
import edu.pay.exception.general.metrics.MetricsException;
import edu.pay.exception.general.metrics.MetricsOutputException;
import edu.pay.exception.general.processor.ProcessFailureException;
import edu.pay.metrics.dataoutputter.MetricsOutputFactory;
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

class SimplePayProcessorImpl implements PayProcessor {

	private final Set<PayError> errors = new HashSet<>();
	private SimplePayMetrics metrics;

	@Override
	public Map<CnpParts, ArrayList<BigDecimal>> process(@NotNull InputStream paymentsInputStream, @NotNull String inputFormat, @NotNull ObjectOutputStream metricsOutputStream, @NotNull String outputFormat) throws ProcessFailureException {
		DataLoaderFactory loaderFactory = new DataLoaderFactory();
		DataLoader loader = loaderFactory.getLoader(inputFormat);
		var dataInput = loader.loadData(paymentsInputStream);
		var mapOfCustomers = ProcessorUtils.getCustomers(dataInput, errors);

		if (dataInput.isEmpty()) {
			Logger.getLogger().logMessage(Logger.LogLevel.INFO, "Empty input.");
			throw new ProcessFailureException("Empty input.");
		}

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
			outputFactory.getWriter(outputFormat).writeToStream(metrics, metricsOutputStream);
		} catch (MetricsException | MetricsOutputException | UnsupportedMetricsTypeException | UnsupportedOutputException e) {
			Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
			throw new ProcessFailureException(e.getMessage());
		}

		return mapOfCustomers;
	}

	@Override
	public PayMetrics getProcessedMetrics() {
		return metrics;
	}

	/**
	 * Visszat??r??ti azon fizet??sek sz??m??t, amelyet 18 ??v alattiak int??ztek.
	 *
	 * @param mapOfCustomers
	 *                          tranzakci??k
	 * @return
	 *          fizet??sek sz??ma
	 */
	private int getPaymentsByMinors(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		final var currentYear = Calendar.getInstance().get(Calendar.YEAR);

		return (int) mapOfCustomers.keySet().stream().filter(k -> (currentYear - k.birthDate().year()) <= 18).count();
	}

	/**
	 * Visszat??r??ti a bukaresti sz??let??s?? rom??n ??llampolg??rok ??ltal int??zett fizet??sek ??sszeg??t.
	 *
	 * @param mapOfCustomers
	 *                          tranzakci??k
	 * @return
	 *          ??sszeg
	 */
	private BigDecimal getTotalAmountCapitalCity(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return mapOfCustomers.keySet().stream().filter(k -> k.county().getAbrv().equals("BU") && !k.foreigner())
						.map(k -> mapOfCustomers.get(k).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * K??lf??ldi szem??lyek sz??ma, akik fizet??st int??ztek.
	 *
	 * @param mapOfCustomers
	 *                          tranzakci??k
	 * @return
	 *          k??lf??ldi szem??lyek sz??ma
	 */
	private Integer getForeigners(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return (int) mapOfCustomers.keySet().stream().filter(CnpParts::foreigner).count();
	}

	/**
	 * Visszat??r??ti a LIMIT-ig ??rv??nyes fizet??sek sz??m??t.
	 *
	 * @param mapOfCustomers
	 *                          tranzakci??k
	 * @param limit
	 *              hat??r??rt??k
	 * @return
	 *          fizet??sek sz??ma
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
	 * Visszat??r??ti a fizet??sek ??tlag??t.
	 *
	 * @param mapOfCustomers
	 *                          tranzakci??k
	 * @return
	 *          fizet??sek ??tlaga
	 */
	private BigDecimal getAverage(final Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return PayUtils.sumTransactions(mapOfCustomers).divide(BigDecimal.valueOf(PayUtils.getTotalTransactionNumber(mapOfCustomers)), 2, RoundingMode.HALF_EVEN);
	}

}
