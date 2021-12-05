package edu.pay.metrics;

import edu.pay.error.PayError;
import edu.pay.exception.general.metrics.MetricsException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

class SimplePayMetricsImpl implements SimplePayMetrics {

    private int foreigners;
    private int paymentsByMinors;
    private int bigPayments;
    private int smallPayments;
    private BigDecimal averagePaymentAmount;
    private BigDecimal totalAmountCapitalCity;
    private Set<PayError> errors;

    /**
     * Létrehoz egy PayMetricsImpl példányt, amely JSON állományként szerializálható
     *
     * @param foreigners
     *                  külföldi személyek száma, akik kifizetést intéztek
     * @param paymentsByMinors
     *                          azon fizetések száma, amelyet 18.-ik életévüket be nem töltött személyek intézték
     * @param bigPayments
     *                      5000 RON-t meghaladó fizetések száma
     * @param smallPayments
     *                      5000 RON-t meg nem haladó fizetések száma, inkluzív
     * @param averagePaymentAmount
     *                              fizetések átlaga
     * @param totalAmountCapitalCity
     *                              bukaresti születésű román állampolgárok által intézett fizetések összege.
     * @param errors
     *              hibatömb
     */
    SimplePayMetricsImpl() {
    }

    /**
     * Külföldi személyek száma, akik intéztek fizetést.
     * @return
     */
    @Override
    public int foreigners() {
        return foreigners;
    }

    /**
     * Kiskorúak által intézett fizetések száma.
     * @return
     */
    @Override
    public int paymentsByMinors() {
        return paymentsByMinors;
    }

    /**
     * 5000 RON fölötti fizetések száma.
     * @return
     */
    @Override
    public int bigPayments() {
        return bigPayments;
    }

    /**
     * 5000 RON alatti fizetések, inkluzív.
     * @return
     */
    @Override
    public int smallPayments() {
        return smallPayments;
    }

    /**
     * Kifizetések átlaga, két tizedes pontossággal.
     */
    @Override
    public BigDecimal averagePaymentAmount() {
        return averagePaymentAmount;
    }

    /**
     * Bukaresti születésű román állampolgárok által intézett kifizetések összege.
     */
    @Override
    public BigDecimal totalAmountCapitalCity() {
        return totalAmountCapitalCity;
    }

    @Override
    public Set<PayError> errors() {
        return errors;
    }

    @Override
    public void setForeigners(int foreigners) {
        this.foreigners = foreigners;
    }

    @Override
    public void setPaymentsByMinors(int paymentsByMinors) {
        this.paymentsByMinors = paymentsByMinors;
    }

    @Override
    public void setBigPayments(int bigPayments) {
        this.bigPayments = bigPayments;
    }

    @Override
    public void setSmallPayments(int smallPayments) {
        this.smallPayments = smallPayments;
    }

    @Override
    public void setAveragePaymentAmount(BigDecimal averagePaymentAmount) {
        this.averagePaymentAmount = averagePaymentAmount;
    }

    @Override
    public void setTotalAmountCapitalCity(BigDecimal totalAmountCapitalCity) {
        this.totalAmountCapitalCity = totalAmountCapitalCity;
    }

    @Override
    public void setErrors(Set<PayError> errors) {
        this.errors = errors;
    }

    @Override
    public void setAllMetricesNullOrEmpty() {
        this.foreigners = 0;
        this.bigPayments = 0;
        this.smallPayments = 0;
        this.averagePaymentAmount = BigDecimal.ZERO;
        this.totalAmountCapitalCity = BigDecimal.ZERO;
        this.errors = Collections.emptySet();
    }

}
