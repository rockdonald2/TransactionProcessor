package edu.cnp.parts;

import edu.cnp.exception.cnp.CnpException;
import edu.cnp.exception.cnp.InvalidSexException;

import java.io.Serializable;

public enum Sex implements Serializable {

	M,
	F,
	U;

	/**
	 * Ellenőrzi és visszatéríti a személy nemét
	 *
	 * @param code
	 *            ellenőrizendő CNP alkotóelem
	 * @return
	 *         nem
	 * @throws InvalidSexException
	 *                      ha a CNP érvénytelen
	 */
	public static Sex getByCode(final String code) throws CnpException {
		try {
			return switch (Byte.parseByte(code)) {
				case 1, 3, 5, 7 -> Sex.M;
				case 2, 4, 6, 8 -> Sex.F;
				case 9 -> Sex.U;
				default -> throw new InvalidSexException("Invalid sex code");
			};
		} catch (Exception e) {
			throw new InvalidSexException("Invalid sex code");
		}
	}

}
