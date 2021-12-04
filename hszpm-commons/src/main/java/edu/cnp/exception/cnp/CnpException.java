package edu.cnp.exception.cnp;

public abstract class CnpException extends Exception {

    private final ErrorCode m_code;

    public enum ErrorCode {

        INVALID_CNP("INVALID_CNP", 1);

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
     * CNP feldolgozása során keletkezett kivétel.
     *
     * @param errorMsg
     *              a kivételhez tartozó hibaüzenet, ami minden esetben "Error:" előtaggal kezdődik
     * @param code
     *              hozzátartozó hibakód
     */
    public CnpException(String errorMsg, ErrorCode code) {
        super("Error: " + errorMsg);
        m_code = code;
    }

    /**
     * Copy-konstruktőr.
     *
     * @param e
     *          lemásolandó kivétel
     */
    public CnpException(CnpException e) {
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
     * @return hibakód típus
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
