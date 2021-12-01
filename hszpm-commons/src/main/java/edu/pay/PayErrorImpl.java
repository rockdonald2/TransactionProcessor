package edu.pay;

import org.json.JSONObject;

class PayErrorImpl implements PayError {

    private final int lineNumber;
    private final int code;

    /**
     * Létrehoz egy PayError példányt, amely a hiba információt fogja tartalmazni, ami a feldolgozás során felmerült.
     *
     * @param lineNumber
     *                      sorszám, ahol előfordult a hiba
     * @param code
     *              hiba típusa
     */
    public PayErrorImpl(int lineNumber, int code) {
        this.lineNumber = lineNumber;
        this.code = code;
    }

    @Override
    public Integer line() {
        return lineNumber;
    }

    @Override
    public Integer type() {
        return code;
    }

    @Override
    public JSONObject getJsonObject() {
        var outJsonFormat = new JSONObject();

        outJsonFormat.put("line", this.line());
        outJsonFormat.put("type", this.type());

        return outJsonFormat;
    }

}
