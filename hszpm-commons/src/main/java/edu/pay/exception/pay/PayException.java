package edu.pay.exception.pay;

public abstract class PayException extends RuntimeException {

    private final ErrorCode code;

    public enum ErrorCode {

        INVALID_LINE("INVALID_LINE", 0),
        INVALID_PAYMENT("INVALID_PAYMENT", 2);

        private final String errorName;
        private final int typeNumber;

        ErrorCode(String errorName, int typeNumber) {
            this.errorName = errorName;
            this.typeNumber = typeNumber;
        }

        public String getErrorName() {
            return errorName;
        }

        public int getErrorTypeNumber() {
            return typeNumber;
        }

    }

    /**
     * Feldolgozáskor felmerült kivételek.
     *
     * @param errorMsg
     *              a kivételhez tartozó hibaüzenet, minden esetben az "Error:" előtaggal kezdődik
     * @param code
     *              hozzátartozó hibakód
     */
    public PayException(String errorMsg, ErrorCode code) {
        super("Error: " + errorMsg);
        this.code = code;
    }

    /**
     * Copy-konstruktőr
     *
     * @param e
     *          lemásolandó kivétel
     */
    public PayException(PayException e) {
        super(e.getMessage());
        code = e.getCode();
    }

    /**
     * Visszatéríti a kivételhez tartozó hibakód példányt.
     *
     * @return ErrorCode
     */
    public ErrorCode getCode() {
        return code;
    }

    /**
     * Visszatéríti a kivételhez tartozó hibakódot.
     *
     * @return hibakód típusszáma
     */
    public int getCodeType() {
        return code.getErrorTypeNumber();
    }

    /**
     * Visszatéríti a kivételhez tartozó hibakód megnevezését.
     *
     * @return hibakód megnevezése
     */
    public String getCodeName() { return code.getErrorName(); }

}

