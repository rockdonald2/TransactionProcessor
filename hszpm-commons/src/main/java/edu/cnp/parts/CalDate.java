package edu.cnp.parts;

import java.io.Serializable;

public abstract class CalDate implements Serializable {

    public abstract Short year();

    public abstract Byte month();

    public abstract Byte day();

    public static class Builder {

        private String date;
        private short year;
        private byte month;
        private byte day;

        public Builder() {}

        public Builder ofDate(String date) {
            var dateElements = date.split("-");
            this.year = Short.parseShort(dateElements[0]);
            this.month = Byte.parseByte(dateElements[1]);
            this.day = Byte.parseByte(dateElements[2]);

            return this;
        }

        public CalDate build() {
            return new CalDateImpl(year, month, day);
        }

    }

}
