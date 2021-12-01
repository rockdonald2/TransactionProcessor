package edu.cnp;

import edu.cnp.exception.CnpException;

public interface CnpValidator {

    /**
     * Validálja a paraméterként kapott CNP-t, és visszatéríti értelmezhető formátumban.
     *
     * @param cnp
     *            ellenőrízendő CNP
     * @return CNP alkotóelemei értelmezhető objektumként
     * @throws CnpException
     *             ha a CNP nem érvényes
     */
    CnpParts validateCnp(String cnp) throws CnpException;

    /**
     * Visszatérít egy használható CnpValidator-t.
     * @return CnpValidator
     */
    static CnpValidator getValidator() {
        return new CnpValidatorImpl();
    }

}