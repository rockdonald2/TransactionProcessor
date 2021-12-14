package edu.cnp.parts;

import java.io.Serializable;

public abstract class CnpParts implements Serializable {

    /**
     * CNP első számjegye alapján kikövetkeztett nem.
     */
    public abstract Sex sex();

    /**
     * Külföldi állampolgár-e.
     *
     * @return {@code true} ha külföldi, {@code false} ellenkező esetben
     */
    public abstract Boolean foreigner();

    /**
     * Születési megye.
     */
    public abstract County county();

    /**
     * Születési dátum.
     */
    public abstract CalDate birthDate();

    /**
     * Sorszám.
     */
    public abstract Short orderNumber();

    public abstract String cnp();
    
    public static String[] getParts() {
        return new String[] { "Sex", "Foreigner", "County", "Birth Date", "CNP" };
    }

    public abstract String[] toStringParts();

    public static class Builder {

        private Sex sex;
        private Boolean foreigner;
        private County county;
        private CalDate birthDate;
        private Short orderNumber;
        private String cnp;

        public Builder() {}

        public Builder ofSex(Sex sex) {
            this.sex = sex;
            return this;
        }

        public Builder isForeigner(boolean foreigner) {
            this.foreigner = foreigner;
            return this;
        }

        public Builder inCounty(County county) {
            this.county = county;
            return this;
        }

        public Builder withBirth(CalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder withOrder(short orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Builder withCnp(String cnp) {
            this.cnp = cnp;
            return this;
        }

        public CnpParts build() {
            return new CnpPartsImpl(sex, foreigner, birthDate, county, orderNumber, cnp);
        }

    }

}
