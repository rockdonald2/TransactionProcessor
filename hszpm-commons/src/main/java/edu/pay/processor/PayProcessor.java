package edu.pay.processor;

import edu.cnp.parts.CnpParts;
import edu.pay.metrics.PayMetrics;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public interface PayProcessor {

    /**
     * Feldolgozza a {@code paymentsInputStream}-ban található tranzakciókat és kiírja a mutatókat a
     * {@code metricsOutputStream}-ba
     *
     * @param paymentsInputStream
     *             csv állomány a tranzakciókkal
     * @param metricsOutputStream
     *             állomány elérési útvonal, ahová szerializálja a mutatókat és a hozzátartozó hibákat
     * @throws IOException
     *             ha valamilyen I/O hiba jelenne meg
     */
    Map<CnpParts, ArrayList<BigDecimal>> process(@NotNull InputStream paymentsInputStream, @NotNull String inputFormat, @NotNull ObjectOutputStream metricsOutputStream, @NotNull String outputFormat);

    PayMetrics getProcessedMetrics();

}
