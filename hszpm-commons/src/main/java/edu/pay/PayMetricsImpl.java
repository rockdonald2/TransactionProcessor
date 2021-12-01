package edu.pay;

import edu.utils.Logger;
import org.json.*;

import java.io.*;
import java.math.BigDecimal;
import java.util.Set;

class PayMetricsImpl implements PayMetrics {

    private final int foreigners;
    private final int paymentsByMinors;
    private final int bigPayments;
    private final int smallPayments;
    private final BigDecimal averagePaymentAmount;
    private final BigDecimal totalAmountCapitalCity;
    private final Set<PayError> errors;

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
    PayMetricsImpl(int foreigners, int paymentsByMinors, int bigPayments,
                   int smallPayments, BigDecimal averagePaymentAmount,
                   BigDecimal totalAmountCapitalCity, Set<PayError> errors) {
        this.foreigners = foreigners;
        this.paymentsByMinors = paymentsByMinors;
        this.bigPayments = bigPayments;
        this.smallPayments = smallPayments;
        this.averagePaymentAmount = averagePaymentAmount;
        this.totalAmountCapitalCity = totalAmountCapitalCity;
        this.errors = errors;
    }

    @Override
    public Integer foreigners() {
        return foreigners;
    }

    @Override
    public Integer paymentsByMinors() {
        return paymentsByMinors;
    }

    @Override
    public Integer bigPayments() {
        return bigPayments;
    }

    @Override
    public Integer smallPayments() {
        return smallPayments;
    }

    @Override
    public BigDecimal averagePaymentAmount() {
        return averagePaymentAmount;
    }

    @Override
    public BigDecimal totalAmountCapitalCity() {
        return totalAmountCapitalCity;
    }

    @Override
    public Set<PayError> errors() {
        return errors;
    }

    public void writeToFile(FileOutputStream file) {
        var metrics = new JSONObject();

        metrics.put("averagePaymentAmount", this.averagePaymentAmount());
        metrics.put("smallPayments", this.smallPayments());
        metrics.put("bigPayments", this.bigPayments());
        metrics.put("paymentsByMinor", this.paymentsByMinors());
        metrics.put("totalAmountCapitalCity", this.totalAmountCapitalCity());
        metrics.put("foreigners", this.foreigners());

        var errs = new JSONArray();
        for (var e : errors()) {
            errs.put(e.getJsonObject());
        }
        metrics.put("errors", errs);

        var o = new OutputStreamWriter(file);
        try {
           o.write(metrics.toString());
           o.flush();
           o.close();
        } catch (IOException e) {
            Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
        }
    }

}
