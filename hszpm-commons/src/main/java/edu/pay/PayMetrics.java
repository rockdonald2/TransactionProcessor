package edu.pay;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

public interface PayMetrics extends Serializable {

    /**
     * Visszatérít egy PayMetrics instanciát a paraméterként megadott állapottal.
     * @param foreigners
     *                   külföldi személyek által intézett fizetések száma
     * @param paymentsByMinors
     *                          fiatalkorúak által intézett fizetések száma
     * @param bigPayments
     *                      5000 RON fölötti fizetések száma
     * @param smallPayments
     *                      5000 RON alatti fizetések száma
     * @param averagePaymentAmount
     *                              átlagfizetés
     * @param totalAmountCapitalCity
     *                              összfizetés, olyan kliensek, akik Bukarestben születtek
     * @param errors
     *              feldolgozási hibák
     * @return PayMetrics
     */
    static PayMetrics getMetrics(int foreigners, int paymentsByMinors, int bigPayments,
                                 int smallPayments, BigDecimal averagePaymentAmount,
                                 BigDecimal totalAmountCapitalCity, Set<PayError> errors) {
        return new PayMetricsImpl(foreigners, paymentsByMinors, bigPayments, smallPayments, averagePaymentAmount, totalAmountCapitalCity, errors);
    }

    /**
     * Külföldi személyek száma, akik intéztek fizetést.
     */
    Integer foreigners();

    /**
     * Kiskorúak által intézett fizetések száma.
     */
    Integer paymentsByMinors();

    /**
     * 5000 RON fölötti fizetések száma.
     */
    Integer bigPayments();

    /**
     * 5000 RON alatti fizetések, inkluzív.
     */
    Integer smallPayments();

    /**
     * Kifizetések átlaga, két tizedes pontossággal.
     */
    BigDecimal averagePaymentAmount();

    /**
     * Bukaresti születésű román állampolgárok által intézett kifizetések összege.
     */
    BigDecimal totalAmountCapitalCity();

    /**
     * Feldolgozási hibák.
     */
    Set<PayError> errors();

    /**
     * Kiírja formázott JSON-ként az adott PayMetrics instancia állapotát a megadott File-ba.
     * @param metricsOutputStream
     *                              kimeneti File adatfolyama
     */
    void writeToFile(FileOutputStream metricsOutputStream);

}