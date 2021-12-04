package edu.pay.error;

import org.json.JSONObject;

class PayErrorImpl extends PayError {

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
    PayErrorImpl(int lineNumber, int code) {
        this.lineNumber = lineNumber;
        this.code = code;
    }

    @Override
    public int getLine() {
        return lineNumber;
    }

    @Override
    public int getType() {
        return code;
    }

    @Override
    public JSONObject generateJson() {
        var outJsonFormat = new JSONObject();

        outJsonFormat.put("line", this.getLine());
        outJsonFormat.put("type", this.getType());

        return outJsonFormat;
    }

}
