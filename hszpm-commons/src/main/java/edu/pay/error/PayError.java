package edu.pay.error;

import edu.cnp.exception.cnp.CnpException;
import edu.pay.exception.general.InvalidErrorTypeException;
import edu.pay.exception.general.MissingErrorArgumentException;
import edu.pay.exception.pay.PayException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

public abstract class PayError {

    /**
     * Sorszám, ahol a hiba előfordult.
     */
    public abstract int getLine();

    /**
     * Hibák típusa:
     * <ul>
     * <li>0 érvénytelen sor</li>
     * <li>1 CNP érvénytelen</li>
     * <li>2 fizetett összeg érvénytelen</li>
     * </ul>
     */
    public abstract int getType();

    /**
     * Visszatéríti az adott hibát formázott JSON-ként.
     * @return JSONObject
     */
    public abstract JSONObject generateJson();

    public static class Builder {

        private Integer line;
        private Integer type;

        public Builder() {}

        public Builder atLine(int line) {
            this.line = line;
            return this;
        }

        public Builder withType(int type) throws InvalidErrorTypeException {
            if (Arrays.stream(PayException.ErrorCode.values()).noneMatch(v -> v.getErrorTypeNumber() == type)
                    && Arrays.stream(CnpException.ErrorCode.values()).noneMatch(v -> v.getErrorTypeNumber() == type)) {
                throw new InvalidErrorTypeException("Invalid error type.");
            }

            this.type = type;
            return this;
        }

        public PayError build() throws MissingErrorArgumentException {
            if (Objects.isNull(this.line)) {
                throw new MissingErrorArgumentException("Missing error line.");
            }

            if (Objects.isNull(this.type)) {
                throw new MissingErrorArgumentException("Missing error type.");
            }

            return new PayErrorImpl(this.line, this.type);
        }
    }

}
