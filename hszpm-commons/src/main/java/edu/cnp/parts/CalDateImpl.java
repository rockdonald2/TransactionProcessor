package edu.cnp.parts;

import java.io.Serializable;

class CalDateImpl extends CalDate implements Serializable {

    private final short year;
    private final byte month;
    private final byte day;

    /**
     * Létrehoz egy értelmezhető dátumpéldányt.
     *
     * @param date
     *              dátum "yyyy-MM-dd" formátumban
     */
    CalDateImpl(short year, byte month, byte day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public Short year() {
        return this.year;
    }

    @Override
    public Byte month() {
        return this.month;
    }

    @Override
    public Byte day() {
        return this.day;
    }

    /**
     * Vizualizálja a dátumot "yyyy-MM-dd" formátumban.
     *
     * @return formatált kimenet
     */
    @Override
    public String toString() {
        return this.year + "-" + this.month + "-" + this.day;
    }

}
