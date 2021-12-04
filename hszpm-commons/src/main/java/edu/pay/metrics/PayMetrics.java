package edu.pay.metrics;

import edu.pay.error.PayError;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public interface PayMetrics extends Serializable {

    /**
     * Feldolgozott mutatók
     * @return mutatónév -> &lt;mutatóérték, mutatótípus&gt; hasítótábla
     */
    Map<String, Pair<Object, Type>> metrices();

    /**
     * Feldolgozási hibák.
     */
    Set<PayError> errors();

    void setAllMetricesNullOrEmpty();

}