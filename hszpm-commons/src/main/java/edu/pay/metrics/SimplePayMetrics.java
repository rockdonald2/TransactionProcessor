package edu.pay.metrics;

import edu.pay.error.PayError;

import java.math.BigDecimal;
import java.util.Set;

public interface SimplePayMetrics extends PayMetrics {

	/**
	 * Külföldi személyek száma, akik intéztek fizetést.
	 */
	int foreigners();

	/**
	 * Kiskorúak által intézett fizetések száma.
	 */
	int paymentsByMinors();

	/**
	 * 5000 RON fölötti fizetések száma.
	 */
	int bigPayments();

	/**
	 * 5000 RON alatti fizetések, inkluzív.
	 */
	int smallPayments();

	/**
	 * Kifizetések átlaga, két tizedes pontossággal.
	 */
	BigDecimal averagePaymentAmount();

	/**
	 * Bukaresti születésű román állampolgárok által intézett kifizetések összege.
	 */
	BigDecimal totalAmountCapitalCity();

	void setForeigners(int foreigners);

	void setPaymentsByMinors(int paymentsByMinors);

	void setBigPayments(int bigPayments);

	void setSmallPayments(int smallPayments);

	void setAveragePaymentAmount(BigDecimal averagePaymentAmount);

	void setTotalAmountCapitalCity(BigDecimal totalAmountCapitalCity);

	void setErrors(Set<PayError> errors);

}
