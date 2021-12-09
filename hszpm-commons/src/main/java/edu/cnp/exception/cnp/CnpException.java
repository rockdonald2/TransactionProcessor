package edu.cnp.exception.cnp;

public abstract class CnpException extends RuntimeException {

    private final ErrorCode code;

    public enum ErrorCode {

        INVALID_CNP("INVALID_CNP", 1);

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
     * CNP feldolgozása során keletkezett kivétel.
     *
     * @param errorMsg
     *              a kivételhez tartozó hibaüzenet, ami minden esetben "Error:" előtaggal kezdődik
     * @param code
     *              hozzátartozó hibakód
     */
    public CnpException(String errorMsg, ErrorCode code) {
        super("Error: " + errorMsg);
        this.code = code;
    }

    /**
     * Copy-konstruktőr.
     *
     * @param e
     *          lemásolandó kivétel
     */
    public CnpException(CnpException e) {
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
     * @return hibakód típus
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
