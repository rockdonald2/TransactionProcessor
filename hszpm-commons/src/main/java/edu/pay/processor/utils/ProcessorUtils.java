package edu.pay.processor.utils;

import edu.cnp.parts.CnpParts;
import edu.cnp.CnpValidator;
import edu.cnp.exception.cnp.CnpException;
import edu.pay.error.PayError;
import edu.pay.exception.pay.MissingDataException;
import edu.pay.exception.pay.NegativePaymentException;
import edu.pay.exception.pay.PayException;
import edu.utils.Logger;

import java.math.BigDecimal;
import java.util.*;

public class ProcessorUtils {

	/**
	 * Ellenőrzi a fizetésekhez tartozó CNP-ket, és visszatéríti a fizetéseket.
	 *
	 * @param dataInput
	 *                  CSV állomány sorai
	 * @return
	 *          tranzakciók
	 */
	public static Map<CnpParts, ArrayList<BigDecimal>> getCustomers(final List<String[]> dataInput, Set<PayError> errors) {
		var mapOfCustomers = new HashMap<CnpParts, ArrayList<BigDecimal>>();

		for (int i = 0; i < dataInput.size(); i++) {
			final var currentPayment = dataInput.get(i);

			if (currentPayment.length != 2) {
				continue;
			}

			CnpParts cnp = null;
			BigDecimal paymentAmount;

			try {
				if (currentPayment[0].equals("") || currentPayment[1].equals("")) {
					throw new MissingDataException("Missing data from line");
				}

				paymentAmount = new BigDecimal(currentPayment[1]);

				if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
					throw new NegativePaymentException("Invalid payment with negative value");
				}
			} catch (PayException e) {
				Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
				writeError(i + 1, e.getCodeType(), errors);
				continue;
			}

			for (var j : mapOfCustomers.keySet()) {
				if (j.toString().equals(currentPayment[0])) {
					cnp = j;
				}
			}

			if (cnp == null) {
				try {
					cnp = CnpValidator.validate(currentPayment[0]);
				} catch (CnpException e) {
					Logger.getLogger().logMessage(Logger.LogLevel.ERROR, e.getMessage());
					writeError(i + 1, e.getCodeType(), errors);
					continue;
				}
			}

			if (!mapOfCustomers.containsKey(cnp)) {
				mapOfCustomers.put(cnp, new ArrayList<>());
			}

			mapOfCustomers.get(cnp).add(paymentAmount);
		}

		return mapOfCustomers;
	}

	/**
	 * Kiír egy hibapéldányt a hibatömbbe, együtt annak típusával és sorszámával, ahol a hiba előfordult.
	 *
	 * @param lineNumber
	 *                      sor
	 * @param errorType
	 *                      hibatípus
	 */
	private static void writeError(final int lineNumber, final int errorType, Set<PayError> errors) {
		errors.add(new PayError.Builder().atLine(lineNumber).withType(errorType).build());
	}

}
