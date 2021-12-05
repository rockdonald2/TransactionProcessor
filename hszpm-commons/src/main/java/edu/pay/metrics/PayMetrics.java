package edu.pay.metrics;

import edu.pay.error.PayError;
import edu.pay.exception.general.metrics.MetricsException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface PayMetrics extends Serializable {

    /**
     * Feldolgozott mutatók
     * @return mutatónév -> &lt;mutatóérték, mutatótípus&gt; hasítótábla
     */
    default Map<String, Pair<Object, Type>> metrices() {
        Map<String, Pair<Object, Type>> ret = new HashMap<>();

        Arrays.stream(this.getClass().getDeclaredFields()).forEach(f ->
        {
            try {
                ret.put(f.getName(), new ImmutablePair<>(this.getClass().getMethod(f.getName()).invoke(this), f.getGenericType()));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new MetricsException("Failed to create metrices.");
            }
        });

        return ret;
    }

    /**
     * Feldolgozási hibák.
     */
    Set<PayError> errors();

    void setAllMetricesNullOrEmpty();

}