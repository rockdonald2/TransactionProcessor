package edu.cnp;

import edu.cnp.exception.CnpException;
import edu.cnp.exception.CnpFormatException;

class CnpUtils {

	/**
	 * CNP hossza
	 */
	static final int CNP_LENGTH = 13;
	/**
	 * Súlyok a súlyozott összeg számításához
	 */
	static final byte[] CONTROL_DIGIT_ARRAY = new byte[]{2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};

	/**
	 * Felhasználva a CNP-t alkotó számjegyeket, kiszámolja annak súlyozott összegét.
	 * 		Szükségelteti a CONTROL_DIGIT_ARRAY használatát.
	 * @param cnpDigitArr
	 * 										CNP-t alkotó számjegyek
	 * @return súlyozott összeg
	 */
	static int calculateWeightedSum(byte[] cnpDigitArr) {
		int digitSum = 0;

		for (int i = 0; i < cnpDigitArr.length - 1; i++) {
			digitSum += CONTROL_DIGIT_ARRAY[i] * cnpDigitArr[i];
		}

		return digitSum;
	}

	/**
	 * A CNP-t String-ből tömbbé alakítja.
	 *
	 * @param cnpString
	 *                  átalakítandó CNP
	 * @return
	 *          CNP tömbként
	 */
	static byte[] createByteCnpFromString(final String cnpString) throws CnpException {
		final var cnpDigits = new byte[cnpString.length()];

		for (int i = 0; i < cnpString.length(); i++) {
			try {
				cnpDigits[i] = Byte.parseByte(cnpString.substring(i, i + 1));
			} catch (Exception e) {
				throw new CnpFormatException("Invalid digit");
			}
		}

		return cnpDigits;
	}

	/**
	 * Kikövetkezteti az évszázadot, amelyben született a nem rezidens személy.
	 *
	 * @param dateYearString
	 *                      CNP alkotóeleme, amely a születési év kikövetkeztetésére szolgál
	 * @return
	 *          évszázad
	 */
	static String guessForeignerCentury(final String dateYearString) {
		return Byte.parseByte(dateYearString) <= 21 ? "20" : "19";
	}

	/**
	 * Összeállít egy születési dátumot az azt alkotó karakterekből. Mindenképpen szükségelteti az évszázad különálló megadását.
	 * @param dateCharArr
	 * 										dátumot alkotó számkarakterek
	 * @param centuryString
	 * 											évszázad karakterlánc
	 * @return formatált String
	 */
	static String composeDate(final char[] dateCharArr, final String centuryString) {
		var composedDate = new StringBuilder();

		composedDate.append(centuryString);

		for (int i = 0; i < dateCharArr.length; ++i) {
			composedDate.append(dateCharArr[i]);

			if (i == 1 || i == 3) {
				composedDate.append('-');
			}
		}

		return composedDate.toString();
	}

}
