package edu.cnp;

import edu.cnp.exception.CnpException;
import edu.cnp.exception.InvalidCountyException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum County implements Serializable {

    AB("AB", 1),
    AR("AR", 2),
    AG("AG", 3),
    BC("BC", 4),
    BH("BH", 5),
    BN("BN", 6),
    BT("BT", 7),
    BV("BV", 8),
    BR("BR", 9),
    BZ("BZ", 10),
    CS("CS", 11),
    CJ("CJ", 12),
    CT("CT", 13),
    CV("CV", 14),
    DB("DB", 15),
    DJ("DJ", 16),
    GL("GL", 17),
    GJ("GJ", 18),
    HR("HR", 19),
    HD("HD", 20),
    IL("IL", 21),
    IS("IS", 22),
    IF("IF", 23),
    MM("MM", 24),
    MH("MH", 25),
    MS("MS", 26),
    NT("NT", 27),
    OT("OT", 28),
    PH("PH", 29),
    SM("SM", 30),
    SJ("SJ", 31),
    SB("SB", 32),
    SV("SV", 33),
    TR("TR", 34),
    TM("TM", 35),
    TL("TL", 36),
    VS("VS", 37),
    VL("VL", 38),
    VN("VN", 39),
    BU("BU", 40),
    BU1("BU", 41),
    BU2("BU", 42),
    BU3("BU", 43),
    BU4("BU", 44),
    BU5("BU", 45),
    BU6("BU", 46),
    CL("CL", 51),
    GR("GR", 52);

    private static final Map<Byte, County> map = new HashMap<>();

    // Létrehoz egy Map-et, amelyen keresztül gyorsabban megy az adott megye kikeresése.
    static {
        for (final var j : County.values()) {
            map.put(j.getCode(), j);
        }
    }

    private final String abrv;
    private final byte code;

    County(String abrv, int code) {
        this.abrv = abrv;
        this.code = (byte) code;
    }

    public String getAbrv() {
        return this.abrv;
    }

    public byte getCode() {
        return this.code;
    }

    /**
     * Ellenőrzi és visszatéríti a születési megyét.
     *
     * @param code CNP alkotóeleme, amely a születési dátum kikövetkeztetésére szolgál
     * @return születési megye
     * @throws CnpException ha a CNP érvénytelen
     */
    public static County getByCode(final String code) throws CnpException {
        try {
            if (!map.containsKey(Byte.parseByte(code))) {
                throw new InvalidCountyException("Invalid county code");
            }

            return map.get(Byte.parseByte(code));
        } catch (Exception e) {
            throw new InvalidCountyException("Invalid county code");
        }
    }

}
