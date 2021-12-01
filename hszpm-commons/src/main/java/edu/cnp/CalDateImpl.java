package edu.cnp;

import java.io.Serializable;

class CalDateImpl implements CalDate, Serializable {

    private final short year;
    private final byte month;
    private final byte day;

    /**
     * Létrehoz egy értelmezhető dátumpéldányt.
     *
     * @param date
     *              dátum "yyyy-MM-dd" formátumban
     */
    CalDateImpl(String date) {
        var dateElements = date.split("-");
        this.year = Short.parseShort(dateElements[0]);
        this.month = Byte.parseByte(dateElements[1]);
        this.day = Byte.parseByte(dateElements[2]);
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
