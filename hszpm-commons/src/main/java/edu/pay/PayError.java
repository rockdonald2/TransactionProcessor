package edu.pay;

import org.json.JSONObject;

public interface PayError {

    /**
     * Sorszám, ahol a hiba előfordult.
     */
    Integer line();

    /**
     * Hibák típusa:
     * <ul>
     * <li>0 érvénytelen sor</li>
     * <li>1 CNP érvénytelen</li>
     * <li>2 fizetett összeg érvénytelen</li>
     * </ul>
     */
    Integer type();

    /**
     * Létrehoz egy adott hibát a paraméterként megadott állapottal.
     * @param lineNumber
     *                  sor, ahol a hiba előfordult
     * @param code
     *             a hiba kódja
     * @return PayError
     */
    static PayError generateError(int lineNumber, int code) {
        return new PayErrorImpl(lineNumber, code);
    }

    /**
     * Visszatéríti az adott hibát formázott JSON-ként.
     * @return JSONObject
     */
    JSONObject getJsonObject();

}
