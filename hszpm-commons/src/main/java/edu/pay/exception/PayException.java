package edu.pay.exception;

public abstract class PayException extends Exception {

    private final ErrorCode m_code;

    public enum ErrorCode {

        INVALID_LINE("INVALID_LINE", 0),
        INVALID_PAYMENT("INVALID_PAYMENT", 2);

        private final String m_errorName;
        private final int m_typeNumber;

        ErrorCode(String errorName, int typeNumber) {
            m_errorName = errorName;
            m_typeNumber = typeNumber;
        }

        String getErrorName() {
            return m_errorName;
        }

        int getErrorTypeNumber() {
            return m_typeNumber;
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
        m_code = code;
    }

    /**
     * Copy-konstruktőr
     *
     * @param e
     *          lemásolandó kivétel
     */
    public PayException(PayException e) {
        super(e.getMessage());
        m_code = e.getCode();
    }

    /**
     * Visszatéríti a kivételhez tartozó hibakód példányt.
     *
     * @return ErrorCode
     */
    public ErrorCode getCode() {
        return m_code;
    }

    /**
     * Visszatéríti a kivételhez tartozó hibakódot.
     *
     * @return hibakód típusszáma
     */
    public int getCodeType() {
        return m_code.getErrorTypeNumber();
    }

    /**
     * Visszatéríti a kivételhez tartozó hibakód megnevezését.
     *
     * @return hibakód megnevezése
     */
    public String getCodeName() { return m_code.getErrorName(); }

}

